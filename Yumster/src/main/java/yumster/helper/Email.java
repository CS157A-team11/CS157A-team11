package yumster.helper;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import com.mailgun.util.EmailUtil;

public class Email {
	private static Email instance = null;
	private MailgunMessagesApi mailgunMessagesApi = null;
	
	public static synchronized Email getInstance() {
		if (instance == null)
			instance = new Email();
			instance.getClient();

		return instance;
	}
	
	public synchronized MailgunMessagesApi getClient() {
		if (mailgunMessagesApi == null) {
			mailgunMessagesApi = MailgunClient.config(System.getenv("MAILGUN_API_KEY")).createApi(MailgunMessagesApi.class);
		}
		
		return mailgunMessagesApi;
	}
	
	public boolean sendEmail(String name, String email, String subject, String text) {
		email = "yumster42@gmail.com";
        Message message = Message.builder()
                .from("breadwich@sandbox7e30eeaa52d84d9e935f43243edca190.mailgun.org")
                .to(EmailUtil.nameWithEmail(name, email))
                .subject(subject)
                .text(text)
                .build();
        
        try {
        	@SuppressWarnings("unused")
			MessageResponse messageResponse = mailgunMessagesApi.sendMessage("sandbox7e30eeaa52d84d9e935f43243edca190.mailgun.org", message);
        } catch (Exception exception) {
         	System.err.println(exception.getLocalizedMessage());
         	return false;
        }
        return true;
	}
}