## What Should I Know Before Getting Started?
### The project directory structure is as follows.
* examples - Maven parent examples project containing Maven Terraform example projects
  * tf-s3 - an example Maven Terraform project that contains 2 s3 modules
  * tf-s3-consumer - a Maven Terraform project that consumes tf-s3 as a dependency
* tf-build-tools - Maven parent project that builds all of the tf-maven related code; this is where you run 'mvn install' to validate the build
  * tf-cmd-api - Maven project for a Java API that calls Terraform
  * tf-maven-plugin - Maven project for the tf-maven-plugin plugin
  * tf-s3-srchetype - Maven project for an archetype that sets up a working Maven Terrform project
 
## How Do I Contribute?
1. Create an Issue
  
   If you want to work on the issue, specify in the issue that you would like to submit a contribution in the form of a PR to address the issue.

2. Work on the Issue

   If the issue is accepted as either a Bug or an Enhancement, then it will be available to work, either by you or by another contributor. 
   If you specified in the issue that you wanted to work on the issue then once it's available to work, you may work on the issue. 
   
   _Note: Issues not accepted for work will not have their associated work reviewed or merged into the codebase._
   
   Start working on an issue by forking the repository and making your updates from the 'develop' branch in your fork or a 
   branch off of your fork's 'develop' branch. 
    
   Don't forget to add your name/details as a contributor in the POM.

3. Submit a Pull Request

   Once you have completed the work to resolve the issue, then submit a Pull Request to the 'develop' branch of the terraform-maven repository.
   The PR will trigger a validation of the build.

4. Code Review

   Once your PR is free from merge conflicts and all of the automated checks pass (i.e. build validation + code coverage validation), one of the developers under the deliveredtechnologies project
   will review the PR and leave comments and/or merge the PR.

5. Remediate 
    
   If any issues arise from the code review, please correct them. Once the PR has no more issues, it will be merged.
    
