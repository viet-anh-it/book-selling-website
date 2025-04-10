# book-selling-website

Personal project for graduation from Hanoi University of Industry.

Technologies used for front-end: HTML, CSS, JS, Bootstrap, jQuery, Thymeleaf.
Technologies used for back-end: Java, Spring MVC, Spring Boot, Spring Security, Spring Data JPA, MySQL.

Analyze and design database using dbdiagram.io tool:
https://dbdiagram.io/d/BookStore-v2-add-relationships-67dfb2f775d75cc8441cc1af

Update at the end of the day on 2025-03-23:

- Create Github repo, Spring Boot project, local git repo, connect local to remote repo.
- Integrate Lombok, Spring Data JPA dependency into project.
- Create JPA Entity Classes: Role, Permission, RolePermission.
- Add Spring Data JPA auditing functionality to the classes.

Mid-day update on 2025-03-24:

- Integrate MySQL Driver, Hibernate JPA Metamodel generator into project.
- Add database connection configuration parameters to application.properties
- Create JPA Entity Classes and configure JPA Relationships using JPA Annotations.
- Create essential enums

Mid-day update on 2025-08-04: Complete the following features:

- Authentication and authorization using JWT.
- Sign up.
- Login: create a new access token and refresh token pair.
- Refresh token: create a new access token and refresh token pair, rotate refresh token.
- Logout: put access token into blacklist, delete refresh token in the database.

Mid-day update on 2025-10-04: Refactor code:

- In JwtService interface, createJwt method, change return type from String to Jwt.

Update at the end of the day 2025-10-04:

- Basically complete sending verification email to activate user account when signing up.
