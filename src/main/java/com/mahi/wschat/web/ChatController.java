package com.mahi.wschat.web;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import com.mahi.wschat.pojo.User;
import com.mahi.wschat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.mahi.wschat.domain.ChatMessage;
import com.mahi.wschat.event.LoginEvent;
import com.mahi.wschat.event.ParticipantRepository;


import javax.inject.Inject;


@Controller
public class ChatController {


	
	@Autowired private ParticipantRepository participantRepository;
	
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	UserService userService;


	@SubscribeMapping("/chat.participants")
	public Collection<LoginEvent> retrieveParticipants() {
		Collection<LoginEvent> events = participantRepository.getActiveSessions().values();

		events.stream().map(loginEvent -> {
			String name = loginEvent.getUsername();
			User user = userService.findByName(name);
			if (user == null) {
				System.out.println(user);
				User saveUser = new User(name);
				userService.save(saveUser);
			}
			return loginEvent;
		}).collect(Collectors.toList());

		return events;
	}
	
	@MessageMapping("/chat.message")
	public ChatMessage filterMessage(@Payload ChatMessage message, Principal principal) {
		message.setUsername(principal.getName());
		return message;
	}
	
	@MessageMapping("/chat.private.{username}")
	public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal) {
		message.setUsername(principal.getName());
		simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
	}
}