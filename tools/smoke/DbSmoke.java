package com.example.tools.smoke;

import com.example.repository.impl.AuthorRepositoryImpl;
import com.example.repository.impl.BookRepositoryImpl;
import com.example.repository.impl.BookTransactionRepositoryImpl;
import com.example.repository.impl.CategoryRepositoryImpl;
import com.example.repository.impl.MemberRepositoryImpl;
import com.example.repository.impl.PublisherRepositoryImpl;
import com.example.repository.impl.UserRepositoryImpl;
import com.example.service.AuthorService;
import com.example.service.BookService;
import com.example.service.BookTransactionService;
import com.example.service.CategoryService;
import com.example.service.LoginService;
import com.example.service.MemberService;
import com.example.service.PublisherService;
import com.example.service.UserService;

import java.sql.SQLException;
import java.util.List;

/**
 * Integration test against the live MySQL database. Every service is exercised
 * with the real repository implementations.
 */
public final class DbSmoke {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        UserService userService = new UserService(new UserRepositoryImpl());
        LoginService loginService = new LoginService(new UserRepositoryImpl());
        CategoryService categoryService = new CategoryService(new CategoryRepositoryImpl());
        AuthorService authorService = new AuthorService(new AuthorRepositoryImpl());
        PublisherService publisherService = new PublisherService(new PublisherRepositoryImpl());
        MemberService memberService = new MemberService(new MemberRepositoryImpl());
        BookService bookService = new BookService(new BookRepositoryImpl(), new AuthorRepositoryImpl(),
                new CategoryRepositoryImpl(), new PublisherRepositoryImpl());
        BookTransactionService transactionService = new BookTransactionService(new BookTransactionRepositoryImpl());

        check("users.getUsers()", () -> userService.getUsers().size());
        check("users.searchUsers()", () -> userService.searchUsers("a").size());
        check("login.find()", () -> loginService.login("admin", "admin123", "admin").getUserId());
        check("categories.getCategories()", () -> categoryService.getCategories().size());
        check("categories.searchCategories()", () -> categoryService.searchCategories("a").size());
        check("authors.getAuthors()", () -> authorService.getAuthors().size());
        check("authors.searchAuthors()", () -> authorService.searchAuthors("a").size());
        check("publishers.getPublishers()", () -> publisherService.getPublishers().size());
        check("publishers.searchPublishers()", () -> publisherService.searchPublishers("a").size());
        check("members.getMembers()", () -> memberService.getMembers().size());
        check("members.searchMembers()", () -> memberService.searchMembers("a").size());
        check("books.getBooks()", () -> bookService.getBooks().size());
        check("books.getBooks(filtered)", () -> bookService.getBooks(null, null, null).size());
        check("books.getAuthors()", () -> bookService.getAuthors().size());
        check("books.getCategories()", () -> bookService.getCategories().size());
        check("books.getPublishers()", () -> bookService.getPublishers().size());
        check("transactions.getTransactionsByMemberId(1)", () -> transactionService.getTransactionsByMemberId(1).size());
        check("transactions.getUnreturnedByMemberId(1)", () -> transactionService.getUnreturnedByMemberId(1).size());

        System.out.println(failures == 0 ? "DB OK" : failures + " DB CHECK(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }

    private interface Check {
        int run() throws Exception;
    }

    private static void check(String name, Check check) {
        try {
            int result = check.run();
            System.out.println("PASS  " + name + "  rows=" + result);
        } catch (Throwable t) {
            failures++;
            System.out.println("FAIL  " + name);
            Throwable cause = t;
            while (cause != null) {
                System.out.println("      " + cause.getClass().getName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }
    }
}
