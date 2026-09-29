-- Seed data for LibraryManagementSystemDB
-- Run with:  /Applications/XAMPP/xamppfiles/bin/mysql -u root < seed.sql
--
-- Design notes:
--   * Every foreign key is resolved with a subquery on the parent's natural key
--     (author / publisher / category NAME, book TITLE, member NAME) instead of a
--     hardcoded auto-increment id. Auto-increment values are not stable: a single
--     rolled-back insert permanently burns an id and shifts every later one,
--     which is exactly what broke the first version of this script.
--   * Each insert is guarded with WHERE NOT EXISTS, so the script is idempotent
--     and can be re-run without duplicating rows.
--   * Existing rows are never modified or deleted.
--   * Users.Image is NOT NULL, so every user row supplies a path.
--   * Users.Password is stored and compared in plain text
--     (UserRepositoryImpl.login does `WHERE Name = ? AND Password = ?`),
--     so these are throwaway development credentials.

USE LibraryManagementSystemDB;

-- --------------------------------------------------------------------- Author
INSERT INTO Author (Name) SELECT 'Bjarne Stroustrup' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Bjarne Stroustrup');
INSERT INTO Author (Name) SELECT 'Donald Knuth'     FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Donald Knuth');
INSERT INTO Author (Name) SELECT 'Martin Fowler'    FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Martin Fowler');
INSERT INTO Author (Name) SELECT 'Robert C. Martin' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Robert C. Martin');
INSERT INTO Author (Name) SELECT 'Brian Kernighan'  FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Brian Kernighan');
INSERT INTO Author (Name) SELECT 'Guido van Rossum' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Guido van Rossum');
INSERT INTO Author (Name) SELECT 'Linus Torvalds'   FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Linus Torvalds');
INSERT INTO Author (Name) SELECT 'Erich Gamma'      FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Erich Gamma');
INSERT INTO Author (Name) SELECT 'Grady Booch'      FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Grady Booch');
INSERT INTO Author (Name) SELECT 'Joshua Bloch'     FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Author WHERE Name = 'Joshua Bloch');

-- ------------------------------------------------------------------ Publisher
INSERT INTO Publisher (Name) SELECT 'Addison-Wesley'  FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'Addison-Wesley');
INSERT INTO Publisher (Name) SELECT 'Pearson'         FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'Pearson');
INSERT INTO Publisher (Name) SELECT 'O Reilly Media'  FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'O Reilly Media');
INSERT INTO Publisher (Name) SELECT 'Prentice Hall'   FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'Prentice Hall');
INSERT INTO Publisher (Name) SELECT 'Manning'         FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'Manning');
INSERT INTO Publisher (Name) SELECT 'No Starch Press' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Publisher WHERE Name = 'No Starch Press');

-- ------------------------------------------------------------------- Category
INSERT INTO Category (Name) SELECT 'Programming'         FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Programming');
INSERT INTO Category (Name) SELECT 'Data Structures'     FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Data Structures');
INSERT INTO Category (Name) SELECT 'Software Engineering' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Software Engineering');
INSERT INTO Category (Name) SELECT 'Operating Systems'    FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Operating Systems');
INSERT INTO Category (Name) SELECT 'Databases'            FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Databases');
INSERT INTO Category (Name) SELECT 'Web Development'      FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Category WHERE Name = 'Web Development');

-- ----------------------------------------------------------------------- Books
INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'The C++ Programming Language', '9780321563342',
       (SELECT AuthorId FROM Author WHERE Name = 'Bjarne Stroustrup'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 2013,
       (SELECT CategoryId FROM Category WHERE Name = 'Programming'),
       'The definitive reference on C++, written by the language creator.', 8, 'cpp.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'The C++ Programming Language');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'The Art of Computer Programming, Volume 1', '9780201898811',
       (SELECT AuthorId FROM Author WHERE Name = 'Donald Knuth'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 1997,
       (SELECT CategoryId FROM Category WHERE Name = 'Data Structures'),
       'Fundamental algorithms and data structures, described with precision.', 3, 'taocp.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'The Art of Computer Programming, Volume 1');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Refactoring: Improving the Design of Existing Code', '9780134757599',
       (SELECT AuthorId FROM Author WHERE Name = 'Martin Fowler'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 2018,
       (SELECT CategoryId FROM Category WHERE Name = 'Software Engineering'),
       'A catalogue of behaviour-preserving transformations for legacy code.', 6, 'refactoring.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Refactoring: Improving the Design of Existing Code');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Clean Architecture', '9780134494166',
       (SELECT AuthorId FROM Author WHERE Name = 'Robert C. Martin'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Prentice Hall'), 2017,
       (SELECT CategoryId FROM Category WHERE Name = 'Software Engineering'),
       'Separating business rules from framework details so code survives change.', 7, 'clean-arch.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Clean Architecture');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'The C Programming Language', '9780131103627',
       (SELECT AuthorId FROM Author WHERE Name = 'Brian Kernighan'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 1988,
       (SELECT CategoryId FROM Category WHERE Name = 'Programming'),
       'Kernighan and Ritchie introduce the language still embedded everywhere.', 10, 'the-c-lang.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'The C Programming Language');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Python Crash Course', '9781593279288',
       (SELECT AuthorId FROM Author WHERE Name = 'Guido van Rossum'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Pearson'), 2019,
       (SELECT CategoryId FROM Category WHERE Name = 'Programming'),
       'A hands-on introduction to Python, with projects in the first half.', 9, 'python-crash.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Python Crash Course');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Linux Kernel Development', '9780672329463',
       (SELECT AuthorId FROM Author WHERE Name = 'Linus Torvalds'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Prentice Hall'), 2006,
       (SELECT CategoryId FROM Category WHERE Name = 'Operating Systems'),
       'A guide to the internals, subsystems and process model of the kernel.', 2, 'linux-kernel.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Linux Kernel Development');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Design Patterns', '9780201633610',
       (SELECT AuthorId FROM Author WHERE Name = 'Erich Gamma'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 1994,
       (SELECT CategoryId FROM Category WHERE Name = 'Software Engineering'),
       'The Gang of Four catalogue that gave the industry a shared vocabulary.', 5, 'design-patterns.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Design Patterns');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'The Unified Modeling Language User Guide', '9780134763877',
       (SELECT AuthorId FROM Author WHERE Name = 'Grady Booch'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 1999,
       (SELECT CategoryId FROM Category WHERE Name = 'Software Engineering'),
       'The original UML specification covering diagrams and constraints.', 2, 'uml.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'The Unified Modeling Language User Guide');

INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image)
SELECT 'Effective Java', '9780134685991',
       (SELECT AuthorId FROM Author WHERE Name = 'Joshua Bloch'),
       (SELECT PublisherId FROM Publisher WHERE Name = 'Addison-Wesley'), 2018,
       (SELECT CategoryId FROM Category WHERE Name = 'Programming'),
       'Item-by-item guidance on writing Java that others can maintain.', 6, 'effective-java.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Book WHERE Title = 'Effective Java');


-- -------------------------------------------------------------------- Members
INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Aung Ko Ko', '12/ABC/1234', '2000-05-15', 'Male', 'aungkoko@example.com', '09123456789',
       'No. 12, Main Street, Yangon', '2025-01-10', '2027-01-10', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Aung Ko Ko');

INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Thiri Hlaing', '12/ABC/5678', '1998-11-02', 'Female', 'thirihlaing@example.com', '09234567890',
       'No. 45, Inya Road, Yangon', '2025-03-22', '2027-03-22', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Thiri Hlaing');

INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Zaw Zaw', '12/ABC/9012', '2001-08-30', 'Male', 'zawzaw@example.com', '09345678901',
       'No. 78, Pyay Road, Mandalay', '2025-06-05', '2027-06-05', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Zaw Zaw');

INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Mya Mya', '12/ABC/3456', '1999-02-14', 'Female', 'myamya@example.com', '09456789012',
       'No. 9, University Road, Yangon', '2025-07-19', '2027-07-19', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Mya Mya');

INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Min Min', '12/ABC/7890', '2002-12-01', 'Female', 'minmin@example.com', '09567890123',
       'No. 33, Strand Road, Yangon', '2025-09-01', '2027-09-01', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Min Min');

INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount)
SELECT 'Tun Tun', '12/ABC/1122', '1997-04-23', 'Male', 'tuntun@example.com', '09678901234',
       'No. 21, Dagon Street, Yangon', '2025-10-15', '2027-10-15', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Member WHERE Name = 'Tun Tun');


-- ---------------------------------------------------------------- BookCopies
-- Stock rows for the books added above, matched by title. Existing stock rows
-- (including the ones for the two pre-existing books) are left untouched.
INSERT INTO BookCopies (BookId, Copies)
SELECT b.BookId, b.Copies FROM Book b
WHERE b.Title IN ('The C++ Programming Language', 'The Art of Computer Programming, Volume 1',
                  'Refactoring: Improving the Design of Existing Code', 'Clean Architecture',
                  'The C Programming Language', 'Python Crash Course', 'Linux Kernel Development',
                  'Design Patterns', 'The Unified Modeling Language User Guide', 'Effective Java')
  AND NOT EXISTS (SELECT 1 FROM BookCopies c WHERE c.BookId = b.BookId);

-- ------------------------------------------------------------- Transactions
-- NULL ReturnedDate marks a book that is still on loan.
INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Refactoring: Improving the Design of Existing Code'),
       (SELECT MemberId FROM Member WHERE Name = 'Aung Ko Ko'),
       '2026-07-01', '2026-07-15', '2026-07-14', 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-07-01' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Refactoring: Improving the Design of Existing Code'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Clean Architecture'),
       (SELECT MemberId FROM Member WHERE Name = 'Aung Ko Ko'),
       '2026-08-01', '2026-08-15', '2026-08-20', 5.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-08-01' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Clean Architecture'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'The C Programming Language'),
       (SELECT MemberId FROM Member WHERE Name = 'Thiri Hlaing'),
       '2026-08-10', '2026-08-24', '2026-08-23', 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-08-10' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'The C Programming Language'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Python Crash Course'),
       (SELECT MemberId FROM Member WHERE Name = 'Thiri Hlaing'),
       '2026-09-01', '2026-09-15', NULL, 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-09-01' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Python Crash Course'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Linux Kernel Development'),
       (SELECT MemberId FROM Member WHERE Name = 'Zaw Zaw'),
       '2026-08-05', '2026-08-19', '2026-08-18', 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-08-05' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Linux Kernel Development'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Design Patterns'),
       (SELECT MemberId FROM Member WHERE Name = 'Zaw Zaw'),
       '2026-09-05', '2026-09-19', NULL, 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-09-05' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Design Patterns'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'The Unified Modeling Language User Guide'),
       (SELECT MemberId FROM Member WHERE Name = 'Mya Mya'),
       '2026-07-20', '2026-08-03', '2026-08-10', 7.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-07-20' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'The Unified Modeling Language User Guide'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'Effective Java'),
       (SELECT MemberId FROM Member WHERE Name = 'Min Min'),
       '2026-09-10', '2026-09-24', NULL, 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-09-10' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'Effective Java'));

INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine)
SELECT (SELECT BookId FROM Book WHERE Title = 'The C Programming Language'),
       (SELECT MemberId FROM Member WHERE Name = 'Tun Tun'),
       '2026-08-25', '2026-09-08', NULL, 0.00
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM BookTransactions t
                  WHERE t.IssuedDate = '2026-08-25' AND t.BookId = (SELECT BookId FROM Book WHERE Title = 'The C Programming Language'));


-- ---------------------------------------------------------------------- Users
-- Image is NOT NULL in this table, so every row carries a path.
-- Passwords are compared in plain text by UserRepositoryImpl.login, so these are
-- development-only credentials. Change them before any real deployment.
INSERT INTO Users (Name, Password, DateOfBirth, Gender, Email, PhoneNo, Role, Address, JoinedDate, Image)
SELECT 'admin', 'admin123', '1990-01-15', 'Male', 'admin@example.com', '09111111111', 'admin',
       'Library HQ, Yangon', '2024-01-02', 'admin.png'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Users WHERE Name = 'admin');

INSERT INTO Users (Name, Password, DateOfBirth, Gender, Email, PhoneNo, Role, Address, JoinedDate, Image)
SELECT 'librarian', 'lib123', '1992-06-20', 'Female', 'librarian@example.com', '09222222222', 'Librarian',
       'Library HQ, Yangon', '2024-02-14', 'librarian.png'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Users WHERE Name = 'librarian');

INSERT INTO Users (Name, Password, DateOfBirth, Gender, Email, PhoneNo, Role, Address, JoinedDate, Image)
SELECT 'assistant', 'asst123', '1995-09-08', 'Female', 'assistant@example.com', '09333333333', 'Librarian',
       'Library HQ, Yangon', '2025-04-01', 'assistant.png'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM Users WHERE Name = 'assistant');

