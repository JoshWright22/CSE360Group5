# CSE360 Team Project 1 - Group 5

## About

This repository contains the source code, tests, and documentation for the CSE360 Team Project (Fall 2025). The project implements a secure, role-based question-and-answer system for ASU students. Key goals include:

* Secure login and identity management
* User account management (name, email, password)
* Role-based functionality (New User, Admin, Reviewer, Instructor, Staff)
* Input validation for all fields
* Automated testing using JUnit
* Screencasts and team coordination documentation

## Repository Structure

```
CSE360Group5/
├─ src/application/        # JavaFX source code
├─ tests/application/      # JUnit test cases
├─ screencasts/            # Screencasts and stand-up recordings
├─ .classpath
├─ .project
├─ .settings/
└─ README.md               # Project README
```

## Contributors

* Joshua Wright (@JoshWright22)
* Kyle Ferolito (@kferolito)
* Jarod Wagner (@Jarod-Wagner)
* Steven Grisham
* Jad Khayyati
* AceCodesx13

## Installation & Usage

1. Clone the repository:

```bash
git clone https://github.com/JoshWright22/CSE360Group5.git
```

2. Open the project in a Java IDE supporting JavaFX (Eclipse, IntelliJ, or NetBeans).
3. Build the project.
4. Run the main application located in `src/application/`.
5. Run JUnit tests from `tests/application/` to verify all functionalities.

## Deliverables

* Source Code: `src/application/`
* JUnit Tests: `tests/application/`
* Recordings: Screencasts demonstrating functional requirements and team collaboration
* PDF Submission: `TP1-Submission-Template.docx`

## Project Tasks

* Implemented robust input validation for all input fields.
* Integrated user stories from HW1 and TP1 assignments.
* Established team norms and conducted stand-up meetings twice per week.
* Developed automated tests to verify functional requirements.
* Produced two screencasts:

  * Technical screencast showing code execution and explanation
  * User-focused screencast showing application usage
* Documented project following the provided code style and internal documentation format.

## User Interface & Experience

* Design inspired by professional applications requiring login and account management.
* Emphasis on clarity, ease of use, and accessibility.
* Error messages for invalid input provide clear guidance to users.

## Input Validation

* All input fields validate format and content before processing.
* Invalid input triggers meaningful error messages.
* Common validation logic factored into helper methods for reusability.

## Testing

* Automated tests implemented using JUnit.
* Each test verifies a specific functional requirement.
* Test results documented and included in repository.

## Team Coordination

* Weekly stand-up meetings recorded in `Recordings/`.
* Progress, issues, and next steps documented after each meeting.
* All team members equally contributed to coding, testing, and screencasts.

## References

* Tuckman's stages of group development: [Wikipedia](https://en.wikipedia.org/wiki/Tuckman%27s_stages_of_group_development)

## License

Educational use only, for CSE360 at Arizona State University.
