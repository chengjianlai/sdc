package org.openecomp.sdc.be.model;

import org.junit.Test;

import java.util.List;


public class ProductTest {

	private Product createTestSubject() {
		return new Product();
	}

	@Test
	public void testCtor() throws Exception {
		new Product(new ProductMetadataDefinition());
	}
	
	@Test
	public void testGetFullName() throws Exception {
		Product testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getFullName();
	}

	
	@Test
	public void testSetFullName() throws Exception {
		Product testSubject;
		String fullName = "";

		// default test
		testSubject = createTestSubject();
		testSubject.setFullName(fullName);
	}

	
	@Test
	public void testGetInvariantUUID() throws Exception {
		Product testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getInvariantUUID();
	}

	
	@Test
	public void testSetInvariantUUID() throws Exception {
		Product testSubject;
		String invariantUUID = "";

		// default test
		testSubject = createTestSubject();
		testSubject.setInvariantUUID(invariantUUID);
	}

	
	@Test
	public void testGetContacts() throws Exception {
		Product testSubject;
		List<String> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getContacts();
	}

	
	@Test
	public void testSetContacts() throws Exception {
		Product testSubject;
		List<String> contacts = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setContacts(contacts);
	}

	
	@Test
	public void testAddContact() throws Exception {
		Product testSubject;
		String contact = "";

		// default test
		testSubject = createTestSubject();
		testSubject.addContact(contact);
	}

	
	@Test
	public void testGetIsActive() throws Exception {
		Product testSubject;
		Boolean result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getIsActive();
	}

	
	@Test
	public void testSetIsActive() throws Exception {
		Product testSubject;
		Boolean isActive = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setIsActive(isActive);
	}

	

}