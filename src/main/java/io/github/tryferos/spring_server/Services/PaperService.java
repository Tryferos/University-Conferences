package io.github.tryferos.spring_server.Services;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Paper.Paper;
import io.github.tryferos.spring_server.Paper.PaperCreationRecord;
import io.github.tryferos.spring_server.Paper.PaperException;
import io.github.tryferos.spring_server.Paper.PaperUpdateRecord;
import io.github.tryferos.spring_server.Repositories.AuthorRepo;
import io.github.tryferos.spring_server.Repositories.PaperRepo;
import io.github.tryferos.spring_server.User.Author;
import io.github.tryferos.spring_server.User.User;
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

    @Autowired
    public AuthorRepo authorRepo;

    @Autowired
    public ConferenceService conferenceService;

    public Paper getPaper(long id){
        return repo.getReferenceById(id);
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
