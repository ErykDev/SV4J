package org.sv4j;

import org.junit.Test;
import org.sv4j.exc.MessageReadingException;

import static org.junit.Assert.*;

/**
 * Testing Messages
 */
public class MessageValidatorTest {

    private static MessageValidator mv = new MessageValidator();

    @Test
    public void isSpam() throws MessageReadingException {
        assertTrue(mv.getOutputFor("We would like to offer cheapest Viagra in the world!").isSpam());
        assertFalse(mv.getOutputFor("We would like to offer you an internship program for students. We have the pleasure of having many of you from Universities from all over the world as our interns. The Little Match Girl project has been in operation for 16 years. Our main idea is to raise funds for charity purposes (to help children living in poverty) by promoting the sale of Fairy Matches. This may take different forms: from simple activities where children pack the shopping for customers at a supermarket, and offer matches to them, to major events like shows, performances, spring-break and summer vacations. It is an excellent opportunity to gain and improve your experience in working with children from elementary and high schools. Our internship program is intended mainly for students of management, marketing, sociology, pedagogy, law, administration, and physical education university departments as well as for those who are planning to work with children in the future.").isSpam());
        assertTrue(mv.getOutputFor("This is the 2nd time we have tried 2 contact u. U have won the £750 Pound prize. 2 claim is easy, call 087187272008 NOW1!").isSpam());
    }
}