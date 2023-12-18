package io.github.tryferos.spring_server;

public final class ErrorMessages {
    public static final String UniqueConstraint = "ERROR: There is another %s with the same %s.";
    public static final String NoNullConstraint = "ERROR: Value for field %s cannot be null.";
    public static final String ForeignKeyConstraint = "ERROR: %s is either null or does not exist.";
    public static final String UserNotExist = "ERROR: User with id %s does not exist";
    public static final String UserCreator = "ERROR: User with id %s is the creator for this conference";
    public static final String UserHasRole = "ERROR: User with id %s already has the role %s for this conference";
    public static final String ConferenceNotExist = "ERROR: Conference with id %s does not exist!";
    public static final String DeleteOperationWrongState= "ERROR: Conference with id %s must be in CREATED state in order to be deleted";
    public static final String ConferenceInFinalState= "ERROR: Conference with id %s is already in final state";
    public static final String ConferenceDeleteNoAccess= "ERROR: You need to be a pc chair for this conference in order to delete it!";
    public static final String PaperCreatorNotIncluded = "ERROR: Paper creator is not included in the name of authors of the paper.";
    public static final String PaperTitleDuplicate = "ERROR: Paper with the title %s, already exists!";

    public static final String NoPermission = "Error: You have no permission to %s %s";

}
