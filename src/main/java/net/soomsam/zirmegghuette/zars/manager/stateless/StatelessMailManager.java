package net.soomsam.zirmegghuette.zars.manager.stateless;

import net.soomsam.zirmegghuette.zars.manager.MailManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("mailManager")
public class StatelessMailManager implements MailManager {
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${javamail.zars.notificationAddress}")
	private String notificationAddress;

	@Override
	public void sendMail(String to, String subject, String text) {
		final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(notificationAddress);
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(text);

		javaMailSender.send(simpleMailMessage);
	}
}
