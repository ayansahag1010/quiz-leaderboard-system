# Quiz Leaderboard System

This project is a Java-based solution for the Bajaj Finserv Health internship assignment.

## Objective
To fetch quiz data from an API, remove duplicate entries, calculate participant scores, and generate a leaderboard.

## Approach
- Polled API 10 times (poll = 0 to 9)
- Maintained 5-second delay between calls
- Removed duplicates using (roundId + participant)
- Aggregated scores using HashMap
- Sorted leaderboard in descending order
- Calculated total score
- Submitted results using POST API

## Tech Used
- Java
- HTTPURLConnection
- JSON Library

## Output
- Leaderboard with participant scores
- Total score of all users

## Note
Duplicate API responses were handled carefully to avoid incorrect score calculation.
