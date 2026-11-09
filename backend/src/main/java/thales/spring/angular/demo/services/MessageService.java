package thales.spring.angular.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.repositories.MessageRepository;

@Service
/**
 * 
 * Service managing the MVC model for message management
 *
 */
public class MessageService {
	
	@Autowired
	private MessageRepository repository;

	/**
	 * 
	 * @return the list of all messages
	 */
	public List<Message> findAll() {
		return repository.findAll();
	}

	/**
	 * Return the message according to its identifier
	 * @param id : message identifier
	 * @return the message if existing
	 */
	public Optional<Message> findById(Long id) {
		return repository.findById(id);
	}

	/**
	 * Delete the message according to its identifier
	 * @param id : message identifier
	 */
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	/**
	 * Delete all messages
	 */
	public void deleteAll() {
		repository.deleteAll();
	}

	/**
	 * Update the message
	 * @param account: the message to update
	 * @return the updated message
	 */
	public Message update(Message message) {
		return repository.save(message);
	}

	/**
	 * Update the list of messages
	 * @param accounts: the messages to be updated
	 * @return updated messages
	 */
	public List<Message> updateAll(List<Message> messages) {
		return repository.saveAll(messages);
	}

	/**
	 * 
	 * @param read : true or false search for read or unread messages
	 * @return returns the list of messages according to the reading criteria (read/unread)
	 */
	public List<Message> findByRead(Boolean read) {
		return repository.findByRead(read);
	}
}
