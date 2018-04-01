package com.mahi.wschat.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import com.mahi.wschat.event.ParticipantRepository;
import com.mahi.wschat.event.PresenceEventListener;

import redis.embedded.RedisServer;

@Configuration
@EnableConfigurationProperties(ChatProperties.class)
public class ChatConfig {

	@Autowired
	private ChatProperties chatProperties;
	
	@Bean
	@Description("Tracks user presence (join / leave) and broacasts it to all connected users")
	public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
		PresenceEventListener presence = new PresenceEventListener(messagingTemplate, participantRepository());
		presence.setLoginDestination(chatProperties.getDestinations().getLogin());
		presence.setLogoutDestination(chatProperties.getDestinations().getLogout());
		return presence;
	}

	@Bean
	@Description("Keeps connected users")
	public ParticipantRepository participantRepository() {
		return new ParticipantRepository();
	}

	
	@Bean(initMethod = "start", destroyMethod = "stop")
	@Description("Embedded Redis used by Spring Session")
	public RedisServer redisServer(@Value("${redis.embedded.port}") int port)  throws IOException {
		return new RedisServer(port);
	}
}
