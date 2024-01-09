package io.github.tryferos.spring_server.Services;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.Conference.ConferenceState;
import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Paper.*;
import io.github.tryferos.spring_server.Repositories.AuthorRepo;
import io.github.tryferos.spring_server.Repositories.PaperRepo;
import io.github.tryferos.spring_server.Repositories.ReviewRepo;
import io.github.tryferos.spring_server.User.Author;
import io.github.tryferos.spring_server.User.User;
import io.github.tryferos.spring_server.User.User_Conference;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PaperService {

    @Autowired
    public PaperRepo repo;

    @Autowired
    public UserService userService;
    public User_ConferenceService userConfService;

    @Autowired
    public AuthorRepo authorRepo;

    @Autowired
    public ConferenceService conferenceService;

    @Autowired
    public ReviewRepo reviewRepo;

    public Paper getPaper(long id){
        return repo.getReferenceById(id);
    }

    public Paper[] searchPapers(String title, String vAbstract, long[] authors){
        Paper[] papers = repo.getPapersByTitleIgnoreCaseOrVAbstractIgnoreCaseOrderByTitle(title, vAbstract);
        return papers;

    }

    public void rejectPaper(long paper_id, long user_id) throws PaperException{
        Paper paper = isConferenceState(paper_id, user_id, ConferenceState.DECISION);
        paper.setState(PaperState.REJECTED);
        repo.save(paper);
    }
    public void approvePaper(long paper_id, long user_id) throws PaperException{
        Paper paper = isConferenceState(paper_id, user_id, ConferenceState.DECISION);
        paper.setState(PaperState.APPROVED);
        repo.save(paper);
    }


    public void finalSubmissionPaper(long paper_id, long user_id, String newContent, Review[] author_comments) throws PaperException{
        Paper paper = isConferenceState(paper_id, user_id, ConferenceState.FINAL_SUBMISSION);
        for(Review rv : author_comments){
            Review review = reviewRepo.getReferenceById(rv.getId());
            if(review.getPaper()==null || review.getPaper().getId()!=paper.getId()) throw new PaperException(ErrorMessages.PaperNotExist, paper.getId());
            if(!review.isCreated()) throw new PaperException(ErrorMessages.PaperReviewerNotAdded, paper.getId());
            review.setAuthor_comment(rv.getAuthor_comment());
            reviewRepo.save(review);
        }
        Review[] reviews = reviewRepo.getReviewsByPaperId(paper.getId());
        if(reviews[0].getAuthor_comment()==null || reviews[1].getAuthor_comment()==null) throw new PaperException(ErrorMessages.PaperAuthorCommentMissing, paper.getId());
        paper.setContent(newContent);
        repo.save(paper);
    }

    public void acceptPaper(long paper_id, long user_id) throws PaperException{
        Paper paper = isConferenceState(paper_id, user_id, ConferenceState.FINAL);
        paper.setState(PaperState.ACCEPTED);
        repo.save(paper);
    }

    private Paper isConferenceState(long paper_id, long user_id, ConferenceState state) throws PaperException {
        Author author = authorMiddleware(user_id, paper_id);
        Paper paper = author.getPaper();
        if(paper==null) throw new PaperException(ErrorMessages.PaperNotExist, paper_id);
        Conference conf = paper.getConference();
        if(conf==null) throw new PaperException(ErrorMessages.ConferenceNotExist, conf.getId());
        if(!conf.getState().equals(state))
            throw new PaperException(ErrorMessages.PaperWrongStateConference, conf.getId(), state);
        return paper;
    }

    public boolean isUserPCMember(long user_id, long paper_id){
        Paper paper = repo.getReferenceById(paper_id);
        User_Conference[] users = userConfService.getConferenceMembers(paper.getConference().getId());
        for (User_Conference user : users) {
            if(user.getUser().getId()==user_id && (user.isPcMember() || user.isPcChair())) return true;
        }
        return false;
    }

    public void reviewPaper(ReviewRecord data, long user_id) throws PaperException{
        Paper paper = repo.getReferenceById(data.paper_id());
        if(!paper.getState().equals(PaperState.SUBMITTED) || !paper.getConference().getState().equals(ConferenceState.REVIEW))
            throw new PaperException(ErrorMessages.PaperWrongStateConference, paper.getConference().getId(), ConferenceState.REVIEW);

        if(!isUserPCMember(user_id, data.paper_id())) throw new PaperException(ErrorMessages.PaperUserNotPcMember, user_id);
        Review review = reviewRepo.getReviewByReviewerId(user_id);
        if(review==null) throw new PaperException(ErrorMessages.PaperReviewerNotAdded, user_id ,data.paper_id());
        review.setComment(data.comment());
        review.setScore(data.score());
        reviewRepo.save(review);
    }

    public void forwardPaper(PaperForwardStateRecord data, long user_id) throws PaperException, PaperStateException{
        Author author = authorMiddleware(user_id, data.paper_id());
        Paper paper = author.getPaper();
        if(paper==null) throw new PaperStateException(ErrorMessages.PaperNotExist, data.paper_id());
        Conference conf = paper.getConference();
        if(conf==null) throw new PaperStateException(ErrorMessages.ConferenceNotExist, conf.getId());

        PaperState state = paper.forwardState(data);
        if(state.equals(PaperState.REVIEWED) && paper.getState().equals(PaperState.REVIEWED)){
            try{
                setReviewers(paper, data.reviewer_id());
            }catch(PaperStateException e){
                paper.setState(PaperState.SUBMITTED);
                throw new PaperStateException(e.getMessage());
            }
        }
    }

    private void setReviewers(Paper paper, long[] reviewer_id) throws PaperException, PaperStateException{
        if(reviewer_id.length==0) throw new PaperStateException(ErrorMessages.PaperNoReviewers, paper.getId());
        Review[] reviews = reviewRepo.getReviewsByPaperId(paper.getId());
        if(reviews!=null && reviews.length>=2) throw new PaperStateException(ErrorMessages.PaperMaxReviewers, paper.getId());
        int reviewers = reviews==null ? 0 : reviews.length;
        for (long id : reviewer_id){
            if(reviewers>=2) throw new PaperException(ErrorMessages.PaperMaxReviewers, paper.getId());
            if(!isUserPCMember(id, paper.getId())) throw new PaperException(ErrorMessages.PaperUserNotPcMember, id);
            Review review = new Review();
            User_Conference userConf = userConfService.getById(paper.getConference().getId(), id);
            review.setReviewer(userConf);
            review.setPaper(paper);
            reviewRepo.save(review);
            repo.save(paper);
            reviewers++;
        }
    }

    private User authorMiddleware(long user_id) throws PaperException{
        User requestor = userService.getUser(user_id);
        if(requestor==null) throw new PaperException(ErrorMessages.UserNotExist, user_id);
        return requestor;
    }
    private Author authorMiddleware(long user_id, long paper_id) throws PaperException{
        User requestor = userService.getUser(user_id);
        if(requestor==null) throw new PaperException(ErrorMessages.UserNotExist, user_id);
        Author author = authorRepo.findAuthorByPaperIdAndUserId(paper_id, user_id);
        if(author==null) throw new PaperException(ErrorMessages.NoPermission, "edit", "this paper");
        return author;
    }

    public Paper editPaper(PaperUpdateRecord data, long user_id) throws PaperException{
        Author author = authorMiddleware(user_id, data.id());
        Paper paper = author.getPaper();
        paper.setContent(data.content()==null ? paper.getContent() : data.content());
        paper.setTitle(data.title()==null ? paper.getTitle() : data.title());
        paper.setVAbstract(data.vAbstract()==null ? paper.getVAbstract() : data.vAbstract());
        paper.setKeywords((data.keywords()==null || data.keywords().length==0) ? paper.getKeywords() : Arrays.stream(
                data.keywords()).reduce(
                (item, prev) -> prev+","+item).get());
        repo.save(paper);
        return author.getPaper();
    }

    public void savePaper(PaperCreationRecord data, long user_id) throws PaperException{
        User requestor = authorMiddleware(user_id);
        String[] authorNames = data.authors();
        int requestorIndex = -1;
        for (int i = 0; i < authorNames.length; i++) {
            if(authorNames[i].toLowerCase().contains(requestor.getFullname().toLowerCase())){
                requestorIndex=i;
                break;
            }
        }
        if(requestorIndex==-1) throw new PaperException(ErrorMessages.PaperCreatorNotIncluded);
        Paper paper = new Paper();
        paper.setTitle(data.title());
        paper.setVAbstract(data.vAbstract());
        Conference conf = conferenceService.getConference(data.conf_id());
        if(conf==null) {
            throw new PaperException(ErrorMessages.ConferenceNotExist, data.conf_id());
        }
        paper.setConference(conf);
        try{
            paper = repo.save(paper);
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("Duplicate entry")) throw new PaperException(ErrorMessages.PaperTitleDuplicate, data.title());
            throw new PaperException("Unknown error", data.title());
        }
        for (int i = 0; i < authorNames.length; i++) {
            Author author = new Author();
            author.setPaper(paper);
            if(i==requestorIndex){
                author.setUser(requestor);
                author.setCreator(true);
            }else{
                author.setUser(userService.getUserByFullName(authorNames[i]));
                author.setCreator(false);
            }
            authorRepo.save(author);
        }
    }

}
