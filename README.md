## BeIT exercise
Project has been build with Java 17 with Spring Boot and Gradle as a build system.

## Loading up data
Application supports file upload via REST endpoints in _.json_ and _.csv_ files.
Uploading data directly is also possible with _/api/measurement/upload_

All upload endpoints return back measurements that have been saved

Two endpoints have been prepared that persist application with provided testing files
- _/api/measurement/demo-full_
- _/api/measurement/demo-scarce_

## Requesting data charts
Request parameters of type OffsetDateType are expected to be in UTC format such as _2022-12-31T23:00:00Z_

## Usage
- Provide data through any of the _POST_ endpoints or use testing data endpoints
- Call a GET on _/api/data_

## Things missing
- Deletion of uploaded files
- DTO objects
- Unit tests