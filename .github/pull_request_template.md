This checklist will help us to incorporate your contribution quickly and easily:

 - [ ] Make sure you have read the [contributing guide](https://github.com/khmarbaise/maven-it-extension/blob/master/CONTRIBUTION.md).
 - [ ] Make sure there is a [issue](https://github.com/khmarbaise/maven-it-extension/issues) filed 
       for the change (usually before you start working on it). Trivial changes like typos do not 
       require a issue.  Your pull request should address just this issue, without 
       pulling in other changes.
 - [ ] Name your branch which contains the pull request accordingly with the issue. 
       Best is to follow: `issue-Number` where `Number` has be replaced with the issue
       number of the appropriate issue you are offering a pull request for.
 - [ ] Each commit in the pull request should have a meaningful subject line and body.
 - [ ] Format the pull request title like `Fixed #Nr - Fixed IT execution.`,
       where you replace `Nr` with the appropriate issue number. It is required 
       to use the issue title in the commit message title and in the first line of the 
       commit message. They should very similar like the following: `Fixed #64 - Automatic site publishing`
       You can of course take a look into the existing commit history.
 - [ ] Make an appropriate entry into the release notes. Follow the examples
       of the existing release notes.
 - [ ] Write a pull request description that is detailed enough to understand what the pull request does, how, and why.
 - [ ] Run `mvn clean verify` to make sure basic checks pass. A more thorough check will 
       be performed on your pull request automatically.
 - [ ] You have run the integration tests successfully (`mvn -Prun-its clean verify`).

If your pull request is about ~20 lines of code you don't need to sign an
[Individual Contributor License Agreement](https://www.apache.org/licenses/icla.pdf) if you are unsure
please ask via a comment on the appropriate isseu.

To make clear that you license your contribution under 
the [Apache License Version 2.0, January 2004](http://www.apache.org/licenses/LICENSE-2.0)
you have to acknowledge this by using the following check-box.

 - [ ] I hereby declare this contribution to be licenced under the [Apache License Version 2.0, January 2004](http://www.apache.org/licenses/LICENSE-2.0)

 - [ ] In any other case, please file an [Apache Individual Contributor License Agreement](https://www.apache.org/licenses/icla.pdf).
