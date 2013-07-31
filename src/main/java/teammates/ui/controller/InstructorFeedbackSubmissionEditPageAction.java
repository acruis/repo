package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		FeedbackSessionAttributes session =
				logic.getFeedbackSession(feedbackSessionName, courseId);		
		InstructorAttributes instructor =
				logic.getInstructorForGoogleId(courseId, account.googleId);
		
		// Verify access level
		new GateKeeper().verifyAccessible(instructor, session, false);
		
		// Get login details
		InstructorFeedbackSubmissionEditPageData data = new InstructorFeedbackSubmissionEditPageData(account);
				
		data.bundle = logic.getFeedbackSessionQuestionsBundle(feedbackSessionName, courseId, instructor.email);
		
		// Check that session is open or private
		if ( session.isOpened() == false &&
			(session.isPrivateSession() && session.creatorEmail.equals(instructor.email)) == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not yet opened for submissions.");
		}
		
		if(data.bundle == null) {
			throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
		}
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
	}

}