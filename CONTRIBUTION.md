<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Contribution Guide

## General Approach

* If you like to contribute to the project please create an [issue first][project-issue] and describe
your intention and furthermore that you like to implement that. 

* If you like to work on an existing issue it is necessary to comment on the appropriate
issue that you like to work on.
 
You should wait for some feedback from the maintainers.

This is needed to get people informed about that someone want to work on that. This is also to
prevent duplicate or overlapping work and wasting your time.

After the feedback you can start by creating an appropriate pull request([Read the paragraph 
about License carefully](#Copyright).) which has to reference the issue you like to work on.

Working on an issue means you have to name the branch accordingly to the issue. This means
your branch has to be named like `issue-#` (`#` has to be replaced with the issue number).

## Coding Style

Actually only a coding style for IDEA IntelliJ exists(The maintainer started with that). If you like to
work with a different IDE's (for example Eclipse) we have to start with creating a coding style configuration
file for Eclipse first before we can continue. We have to make it part of the project(This would
be a great contribution.). 

It would be a good idea having a Checkstyle configuration which could automate the check for the 
correct formatting (Open for contributions here as well).

## Branches

You can work on a branch as you like but to get a branch (pull request) merged you have to squash
your commits into a single commit. After you have squashed your commits into a single commit you 
have to rebase against most recent state of master otherwise we will not merge that into our master.

The goal is to have a single commit which represents the solved issue or implemented feature or fixed
bug. 

It might happen that after you have squashed your commits a review suggests for some changes.
Those changes should be made and afterwards squash your commits into a single one. This results in
a `git push origin --force issue-#` on your branch. This makes it easier to review the change in a 
whole instead of having a lot of small commits etc. 

## Commit Message

The commit message for pull request has to be formatted like this:
```
Fixed #Number - Title of the issue
 - Optional explanations if needed.
```
The `Number` has to be replaced with the real issue number. This makes sure by merging this pull
request the associated issue will be closed automatically.

## Testing

If you are implementing a feature or fixing a bug you have to write unit- and or integration tests 
otherwise we could not integrate changes into the master.

The unit- and integration tests in the project are using [JUnit Jupiter based][junit-jupiter]. 
The assertions have to use [AssertJ][assertj] library only and not the assertions offered by 
[JUnit Jupiter][junit-jupiter].

## Release Notes

Before a change will be merged into master it is necessary to have an appropriate entry
in the release notes which describes what has been done/improved or fixed. This has to be
part of the pull request. You can take a look how currently the release notes are done.

## Copyright

Every contribution you like to make has to be under the license of the project which is
the [Apache License Version 2.0, January 2004][apache-license]. If you don't like 
to contribute under that license than we will not accept that contribution.

During the creation of the pull request the template contains a number of checkboxes please keep them
in (do not delete any of them otherwise we will not accept the pull request) and make the appropriate
checks otherwise we do not accept that contribution.

[apache-license]: https://github.com/khmarbaise/maven-it-extension/blob/master/LICENSE.txt   
[assertj]: https://assertj.github.io/doc/
[junit-jupiter]: https://junit.org/junit5/
[project-issue]: https://github.com/khmarbaise/maven-it-extension/issues