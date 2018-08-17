package com.qthegamep.bookmanager2.entity;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import static org.assertj.core.api.Assertions.*;

public class BookTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;

    private Book firstBook;
    private Book secondBook;

    @Before
    public void setUp() {
        firstBook = new Book();

        firstBook.setId(1);
        firstBook.setName("test firstBook");
        firstBook.setAuthor("test author");
        firstBook.setPrintYear(2000);
        firstBook.setRead(false);

        secondBook = new Book();

        secondBook.setId(1);
        secondBook.setName("test firstBook");
        secondBook.setAuthor("test author");
        secondBook.setPrintYear(2000);
        secondBook.setRead(false);
    }

    @Test
    public void shouldCreateObjectWithNoArgsConstructor() {
        assertThat(firstBook).isNotNull();
    }

    @Test
    public void shouldGetAndSetId() {
        val id = 2;

        firstBook.setId(id);

        assertThat(firstBook.getId()).isEqualTo(id);
    }

    @Test
    public void shouldGetAndSetName() {
        val name = "testBook";

        firstBook.setName(name);

        assertThat(firstBook.getName()).isEqualTo(name);
    }

    @Test
    public void shouldGetAndSetAuthor() {
        val author = "testAuthor";

        firstBook.setAuthor(author);

        assertThat(firstBook.getAuthor()).isEqualTo(author);
    }

    @Test
    public void shouldGetAndSetPrintYear() {
        val printYear = 2010;

        firstBook.setPrintYear(printYear);

        assertThat(firstBook.getPrintYear()).isEqualTo(printYear);
    }

    @Test
    public void shouldGetAndSetIsRead() {
        val isRead = true;

        firstBook.setRead(isRead);

        assertThat(firstBook.isRead()).isEqualTo(isRead);
    }

    @Test
    public void shouldBeEquals() {
        assertThat(firstBook).isEqualTo(secondBook);
    }

    @Test
    public void shouldBeEqualsWithNullName() {
        firstBook.setName(null);
        secondBook.setName(null);

        assertThat(firstBook).isEqualTo(secondBook);
    }

    @Test
    public void shouldBeEqualsWithNullAuthor() {
        firstBook.setAuthor(null);
        secondBook.setAuthor(null);

        assertThat(firstBook).isEqualTo(secondBook);
    }

    @Test
    public void shouldBeEqualsOfCopyObject() {
        secondBook = firstBook;

        assertThat(firstBook).isEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfIdIsNotEquals() {
        secondBook.setId(2);

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfNameOfFirstObjectIsNull() {
        firstBook.setName(null);

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfNameIsNotEquals() {
        secondBook.setName("secondBook");

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfAuthorOfFirstObjectIsNull() {
        firstBook.setAuthor(null);

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfAuthorIsNotEquals() {
        secondBook.setAuthor("newAuthor");

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfPrintYearIsNotEquals() {
        secondBook.setPrintYear(2011);

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfIsReadIsNotEquals() {
        secondBook.setRead(true);

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsToNullObject() {
        secondBook = null;

        assertThat(firstBook).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldBeNotEqualsIfSubClassHasNewContract() {

        @Data
        @EqualsAndHashCode(callSuper = true)
        class BookWithDescription extends Book {

            private String description;
        }

        val bookWithDescription = new BookWithDescription();

        bookWithDescription.setId(1);
        bookWithDescription.setName("testBook");
        bookWithDescription.setAuthor("testAuthor");
        bookWithDescription.setDescription("testDescription");
        bookWithDescription.setPrintYear(2010);
        bookWithDescription.setRead(false);

        assertThat(firstBook).isNotEqualTo(bookWithDescription);
    }

    @Test
    public void shouldBeEqualsHashCode() {
        assertThat(firstBook.hashCode()).isEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeEqualsHashCodeOfCopyObject() {
        secondBook = firstBook;

        assertThat(firstBook.hashCode()).isEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldWorkHashCodeCorrectly() {
        val actual = firstBook.hashCode();

        var expected = 1;
        expected = expected * 59 + firstBook.getId();
        val name = firstBook.getName();
        expected = expected * 59 + (name == null ? 43 : name.hashCode());
        val author = firstBook.getAuthor();
        expected = expected * 59 + (author == null ? 43 : author.hashCode());
        expected = expected * 59 + firstBook.getPrintYear();
        expected = expected * 59 + (firstBook.isRead() ? 79 : 97);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldBeNotEqualsHashCodeIfIdIsNotEquals() {
        secondBook.setId(2);

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeNotEqualsHashCodeIfNameOfFirstObjectIsNull() {
        firstBook.setName(null);

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeNotEqualsHashCodeIfAuthorOfFirstObjectIsNull() {
        firstBook.setAuthor(null);

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeNotEqualsHashCodeIfPrintYearIsNotEquals() {
        secondBook.setPrintYear(2011);

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeNotEqualsHashCodeIfIsReadIsNotEquals() {
        secondBook.setRead(true);

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook.hashCode());
    }

    @Test
    public void shouldBeNotEqualsHashCodeToNullObject() {
        secondBook = null;

        assertThat(firstBook.hashCode()).isNotEqualTo(secondBook);
    }

    @Test
    public void shouldWorkToStringCorrectly() {
        val expected = "Book(id=1, name=test firstBook, author=test author, printYear=2000, isRead=false)";

        assertThat(firstBook.toString()).isEqualTo(expected);
    }
}
