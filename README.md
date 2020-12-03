[tf-maven-plugin]:https://search.maven.org/artifact/com.deliveredtechnologies/tf-maven-plugin/0.12/maven-plugin
[tf-cmd-api]:https://search.maven.org/artifact/com.deliveredtechnologies/tf-cmd-api/0.12/jar
[tf-s3-archetype]:https://search.maven.org/artifact/com.deliveredtechnologies/tf-s3-archetype/0.12/jar
[tf-maven-plugin-snapshot]:https://oss.sonatype.org/content/repositories/snapshots/com/deliveredtechnologies/tf-maven-plugin/
[tf-cmd-api-snapshot]:https://oss.sonatype.org/content/repositories/snapshots/com/deliveredtechnologies/tf-cmd-api/
[tf-s3-archetype-snapshot]:https://oss.sonatype.org/content/repositories/snapshots/com/deliveredtechnologies/tf-s3-archetype/
[maven-badge]:https://img.shields.io/badge/maven%20central-0.12-green.svg
[maven-snapshot-badge]:https://img.shields.io/badge/SNAPSHOT-0.13-green.svg

![terraform-maven](.docs/MavenTerraform.png)

---


![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
[![Build Status](https://github.com/deliveredtechnologies/terraform-maven/workflows/build/badge.svg)](https://github.com/deliveredtechnologies/terraform-maven/actions?query=workflow%3A%22build%22)
[![Coverage Status](https://coveralls.io/repos/github/deliveredtechnologies/terraform-maven/badge.svg?branch=develop)](https://coveralls.io/github/deliveredtechnologies/terraform-maven?branch=develop)
[![Maven Central][maven-badge]][tf-maven-plugin]

# Terraform Maven Plugin

The Terraform Maven Plugin brings Maven to Terraform, which greatly enhances Terraform’s project lifecycle and dependency management capability. 
Maven, in some form has been the standard for Java project management for over a decade. Now, all of that Maven goodness can be used with Terraform!

Integrate automtated tests (Spock, JUnit, TestNG) into your Terraform projects, use Maven's dependency management functionality with Terraform, combine Java and Terraform into the same project, and more! 

<hr>

### Contents

* [Artifacts in This Repository](#artifacts-in-this-repository)
* [Repository Directory Structure](#repo-directory-structure)
* [Benefits of the Terraform Maven Plugin](#benefits-of-the-terraform-maven-plugin)
* [Maven Goals](#maven-goals)
  * [tf:get](#tfget)
  * [tf:init](#tfinit)
  * [tf:plan](#tfplan)
  * [tf:apply](#tfapply)
  * [tf:destroy](#tfdestroy)
  * [tf:package](#tfpackage)
  * [tf:deploy](#tfdeploy)
  * [tf:clean](#tfclean)
  * [tf:wrapper](#tfwrapper)
* [How Commands Are Delegated to Terraform](#how-commands-are-delegated-to-terraform)
  * [*nix-based Operating Systems](#nix-based-operating-systems)
  * [Windows Operating Systems](#windows-operating-systems)
* [Getting Started](https://github.com/deliveredtechnologies/terraform-maven/wiki/Getting-Started)
* [Setting Up a Terraform Maven Project](#setting-up-a-terraform-maven-project)
* [Setting Up a Terraform Maven Project Using an ArcheType](#setting-up-a-terraform-maven-project-using-an-archetype)
* [How to Use Terraform Maven Projects](#how-to-use-terraform-maven-projects)
* [Articles](#articles)

Not finding what you are looking for? [Try the Wiki!](https://github.com/deliveredtechnologies/terraform-maven/wiki)

<hr>

### Artifacts in This Repository

| Artifact Name    | Version | Latest Snapshot | Description |
|------------------|---------|-----------------|-------------|
| tf-maven-plugin  | [![Maven Central][maven-badge]][tf-maven-plugin]  | [![Maven Snapshot][maven-snapshot-badge]][tf-maven-plugin-snapshot]  | Terraform Maven Plugin       |
| tf-cmd-api       | [![Maven Central][maven-badge]][tf-cmd-api]       | [![Maven Snapshot][maven-snapshot-badge]][tf-cmd-api-snapshot]       | Terraform Command API        |
| tf-s3-archetype  | [![Maven Central][maven-badge]][tf-s3-archetype]  | [![Maven Snapshot][maven-snapshot-badge]][tf-s3-archetype-snapshot]  | Maven Terraform S3 Archetype |

### Repository Directory Structure
* examples - Terraform Maven example projects
  * tf-s3 - A Terraform Maven s3 example project
  * tf-s3-consumer - An example project that consumes the tf-s3 project as a dependency
* tf-build-tools - The parent project of the tf-maven-plugin and tf-cmd-api projects
  * tf-maven-plugin - The Terraform Maven Plugin project
  * tf-cmd-api - A Java API for Terraform project
  * tf-test-groovy - A Java library that assists with Testing Terraform using Groovy (e.g. Spock)
  * tf-s3-archetype - An Archetype for a S3 Terraform Project

### Benefits of the Terraform Maven Plugin
* Dependency Management
  * Terraform really has no dependency management to speak of. Even when you use Terraform Cloud, Terraform Enterprise or some other private
    Terraform module registry, a version update to a module means editing the Terraform code in each and every place
    that module is sourced. The Terraform Maven Plugin allows you to specify your dependencies for Terraform the same way
    you specify your dependencies with Java: in an external [Maven POM file](https://maven.apache.org/pom.html). And
    because it's Maven, Terraform modules sourced from Maven repos can also take advantage of version ranges and 
    Maven's SNAPSHOT functionality. It also resolves transitive dependencies. No more modifying code for version updates! No more sourcing obscure URLs!
    Hooray, Maven! 
* Packaging as Part of the Build Lifecycle
  * The [package goal](#tfpackage) can package a Terraform root module for deployment into a Maven repo
    or it can package that same Terraform root module with its dependencies for deployment into an isolated environment
    or Terraform Enterprise. It's all part of a single Maven goal/command.
* Deploying Terraform Artifacts to a Maven Repo with an Attached POM is a Snap
  * By default, the [deploy goal](#tfdeploy) deploys a zip artifact packaged by the [package goal](#tfpackage) to a Maven repo
    along with the POM of the current Maven Terraform project. But if you want to point to a differnt POM or a different
    artifact for deployment, it can do that too. Easy peasy.
* Terraform support for Java testing frameworks
  * The tf-cmd-api artifact provides Java support for Terraform, which can be used to easily integrate
    mature Java testing frameworks, like Spock, JUnit or TestNG. One example of this is the [[tf-s3 example in this repo]](examples/tf-s3).
* Simple Integration with CI Tools
  * Get rid of hundreds of lines of untested code in your CI tool and replace it with tested build lifecycle management
    using Maven! Most CI tools either have Maven included or have a Maven plugin available. Less Terraform build logic 
    in your CI tool means more reliable builds and less CI code to maintain. 
* Build Terraform Maven Projects or Standalone Terraform Configurations and Anything In Between
  * You can use the Terraform Maven plugin for building any Terraform, not just Terraform Maven projects!
    The default configuration is opinionated around the Maven project structure. But that's available to override.
    Do you have a different folder containing Terraform modules that you want packaged into a Fat Zip? No problem.
    What about just running a Terraform configuration in some directory that you specify? That works too. You don't lose
    anything with the Terraform Maven plugin. You just gain a whole lot of packaged functionality and the build lifecycle
    power of Maven with Terraform!
    
### Maven Goals

#### tf:get

Description:

Downloads Maven artifacts into a common modules directory and extracts each artifacts
contents into a folder named for the artifact (version agnostic). 

Optional Parameters:

| Name         | Type   | Description                                                                                        |
| ------------ | ------ | -------------------------------------------------------------------------------------------------- |
| tfModulesDir | String | The directory location where Terraform modules will be expanded; defaults to `src/main/.tfmodules` |

---

#### tf:init

Description:

Executes the `terraform init` command. See [https://www.terraform.io/docs/commands/init.html](https://www.terraform.io/docs/commands/init.html).

Optional Parameters:

| Name          | Type    | Description                                                                                                                |
| ------------- | ------- | -------------------------------------------------------------------------------------------------------------------------- |
| tfRootDir     | String  | The root module directory location where terraform will be initialized; defaults to `src/main/tf/{first dir found}`        |
| pluginDir     | String  | Skips plugin installation and loads plugins only from the specified directory                                              |
| getPlugins    | Boolean | Skips plugin installation                                                                                                  |
| backendConfig | String  | A comma delimited string of optional backend config (e.g. backendConfig="region=us-east-1,bucket=mybucket,key=/some/path") | 
| verifyPlugins | Boolean | Skips release signature validation when installing downloaded plugins (not recommended)                                    |
| skipTfGet     | Boolean | If set to true, tf:init is done without running tf:get                                                                     |
| artifact      | String  | Supplied in form {groupId}:{artifactId}:{versionId}; if present, the maven artifact is treated like a root module          |
| backendType   | String  | It creates default backend (backend.generated.tf.json) with the string value we passed. (e.g. -DbackendType=s3 it creates "backend.generated.tf.json" file with s3 as the backend Type) |

---
 
#### tf:plan

Description:

Executes the `terraform plan` command. See [https://www.terraform.io/docs/commands/plan.html](https://www.terraform.io/docs/commands/plan.html).

Optional Parameters:

| Name           | Type    | Description                                                                                                                                   |
| -------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| tfVarFiles     | String  | A comma delimited string of tfvars files (e.g. -var-file=foo)                                                                                 |
| tfVars         | String  | A comma delimited string of tfvars (e.g. -var 'name=value')                                                                                   |
| lockTimeout    | Number  | Duration to retry a state lock                                                                                                                |
| target         | String  | A resource address to target                                                                                                                  |
| planInput      | Boolean | If set to "true", input variables not directly set will be requested; otherwise, the plan will fail                                           |
| noColor        | Any     | If this property exists, the -no-color flag is set                                                                                            |
| destroyPlan    | Any     | If this property exists, a destroy plan is outputted                                                                                          | 
| planOutputFile | String  | The path to save the generated execution plan. To upload plan to S3 (e.g. -DplanOutputFile=s3://<bucket-name>/<key-prefix>/<plan-file-name>)  |
| kmsKeyId       | String  | To Upload plan to s3 with kms encrypted (e.g. -DkmsKeyId=<kms-Key-ID>)                                                                        | 
| tfRootDir      | String  | A terraform config directory to apply; defaults to `src/main/tf/{first dir found}`, then current directory                                    |
| timeout        | Number  | The maximum time in milliseconds that the terraform apply command can run; defaults to 10min                                                  |
| refreshState   | Boolean | If set to "true" then Terraform will refresh the state before generating the plan                                                             |
| tfState        | String  | The path to the state file; defaults to `terraform.tfstate`                                                                                   |
| artifact       | String  | Supplied in form {groupId}:{artifactId}:{versionId}; if present, the maven artifact is treated like a root module                             |

---

#### tf:apply

Description:

Executes the `terraform apply` command. See [https://www.terraform.io/docs/commands/apply.html](https://www.terraform.io/docs/commands/apply.html).

Optional Parameters:

| Name        | Type   | Description                                                                                                        |
| ----------- | ------ | ------------------------------------------------------------------------------------------------------------------ |
| tfVarFiles  | String | A comma delimited string of tfvars files (e.g. -var-file=foo)                                                      |
| tfVars      | String | A comma delimited string of tfvars (e.g. -var 'name=value')                                                        |
| lockTimeout | Number | Duration to retry a state lock                                                                                     |
| target      | String | A resource address to target                                                                                       |
| noColor     | Any    | If this property exists, the -no-color flag is set                                                                 |
| plan        | String | A terraform plan to apply; if both plan and tfRootDir are specified, only plan is used                             |
| tfRootDir   | String | A terraform config directory to apply; defaults to `src/main/tf/{first dir found}`, then current directory         |
| refreshState | Boolean | If set to "true" then Terraform will refresh the state before apply                                              |
| timeout     | Number | The maximum time in milliseconds that the terraform apply command can run; defaults to 10min                       |
| artifact    | String  | Supplied in form {groupId}:{artifactId}:{versionId}; if present, the maven artifact is treated like a root module |

---

#### tf:destroy

Description:

Executes the `terraform destroy` command. See [https://www.terraform.io/docs/commands/destroy.html](https://www.terraform.io/docs/commands/destroy.html).

Optional Parameters:

| Name        | Type   | Description                                                                                                        |
| ----------- | ------ | ------------------------------------------------------------------------------------------------------------------ |
| lockTimeout | Number | Duration to retry a state lock                                                                                     |
| tfVarFiles  | String | A comma delimited string of tfvars files (e.g. -var-file=foo)                                                      |
| tfVars      | String | A comma delimited string of tfvars (e.g. -var 'name=value')                                                        |
| target      | Number | A resource address to target                                                                                       |
| noColor     | Any    | If this property exists, the -no-color flag is set                                                                 |
| tfRootDir   | String | A terraform config directory to destroy; defaults to current directory                         |
| refreshState | Boolean | If set to "true" then Terraform will refresh the state before apply                                              |
| timeout     | Number | The maximum time in milliseconds that the terraform destroy command can run; defaults to 10min                     |
| artifact    | String  | Supplied in form {groupId}:{artifactId}:{versionId}; if present, the maven artifact is treated like a root module |

---

#### tf:package

Description:

Recursively packages the Terraform files from the root module directory as target/{artifact-id}-{version}.zip.
Optionally, a fat compressed package can be created instead, which also includes the Terraform module dependencies if available; see the [tf:get goal](#tfget) above.

_Note: Within the fat compressed package, module source paths are updated accordingly so that the pacakge is a wholly contained working module that can be consumed as a module,
extracted, initialized and applied as-is or submitted to Terraform Enterprise._

| Name         | Type    | Description                                                                                                                                       |
| ------------ | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------|
| tfRootDir    | String  | The terraform root module directory location; defaults to src/main/tf/{first directory found} or src/main/tf if there are multiple source modules |
| tfModulesDir | String  | The directory that contains the Terraform module depenencies; defaults to src/main/.tfmodules                                                     |
| tfVarFiles   | String  | A comma delimited string of tfvars files in relation to tfRootDir (e.g., variables/dev1.tfvars,variables/dev2.tfvars)                             | 
| fatTar       | Boolean | Set to true if a fat compressed tar.gz package should be created, otherwise false; defaults to false                                              |

---

#### tf:deploy

Description:

Deploys a packaged Terraform zip artifact ([see tf:package](#tfpackage)) with a POM to the specified Maven repo.

| Name         | Type   | Description                                                                                                       |
| ------------ | ------ | ----------------------------------------------------------------------------------------------------------------- |
| file         | String | The name of the Terraform zip file to deploy; defaults to target/{artifactId}-{version}.zip                       |
| url          | String | The url of the Maven repo to which the zip file artifact will be deployed; defaults to {HOME}/.m2/repository      |
| pomFile      | String | The path to the pom.xml file to attach to the artifact; defaults to .flattened-pom.xml in the root of the project |
| generatePom  | String | If set to "true" then a POM will be generated and attached to the deployment                                      |
| groupId      | String | The groupId for the generated POM (only used if generatePom=true                                                  |
| artifactId   | String | The artifactId for the generated POM (only used if generatePom=true                                               |
| version      | String | The version for the generated POM (only used if generatePom=true                                                  |
| repositoryId | String | The server id to map on the <id> under <server> section of settings.xml; typically for authentication             |

---

#### tf:clean

Description:

Deletes all 'terraform' files from terraform configurations along with the Terraform modules directory.

| Name         | Type    | Description                                                                                   |
| ------------ | ------- | --------------------------------------------------------------------------------------------- |
| tfRootDir    | String  | The terraform root module directory location; defaults to src/main/tf/{first directory found} |
| tfModulesDir | String  | The directory that contains the Terraform module depenencies; defaults to src/main/.tfmodules |

#### tf:wrapper

Description:

Binds the Maven project to a specific Terraform binary version (think [Maven Wrapper](https://github.com/takari/maven-wrapper), but for Terraform).

The format of the URL from which to fetch the Terraform binary is modeled after HashiCorp's distribution URL.

```{distributionSite}/{releaseDir}/{releaseVer}/{releaseName}_{releaseVer}_{releaseOS}_{releaseSuffix}```

The default location is 

| Name             | Type   | Description                                                                                        |
| ---------------- | ------ | -------------------------------------------------------------------------------------------------- |
| distributionSite | String | The baseUrl from which to fetch the Terraform binary; defaults to _https://releases.hashicorp.com_ |
| releaseDir       | String | The name of the release project; defaults to _terraform_                                           |
| releaseVer       | String | The version of the Terraform binary to bind to the project                                         |
| releaseName      | String | The name of the release artifact; defaults to _terraform_                                          |
| releaseSuffix    | String | The suffix of the artifact, including extension; defaults to _amd64.zip_                           |

### How Commands Are Delegated to Terraform

Terraform commands are executed in a forked shell that is joined to the Maven process. This has important implications
based on the operating system being used.

#### *nix-based Operating Systems

Terraform commands executed on *nix-based systems are forked into a bash shell that is joined to the Maven process.

#### Windows Operating Systems

Terraform commands executed on Windows operating systems by default use the Windows default command line (cmd.exe),
which is not strictly compatible with Terraform as of Terraform v0.12 (e.g. -var parameters fail).

However, Git Bash can be used in place of the default Windows command line to improve compatibility with Terraform 
commands. To enable Git Bash, either set a Java system property `shellPath` or an environment variable `SHELL_PATH` to 
the absolute path of the Git Bash executable.    

### Setting Up a Terraform Maven Project

Instead of doing all the above steps you can simply build the module/project by running the [Maven Archetype Plugin](https://maven.apache.org/guides/mini/guide-creating-archetypes.html) which creates project from an archetype.

An example on how to generate the project using an archetype is shown below.

```bash
mvn archetype:generate -B -DarchetypeGroupId=com.deliveredtechnologies -DarchetypeArtifactId="tf-s3-archetype" -DarchetypeVersion=0.12 -DgroupId=<custom_group_name> -DartifactId=<custom-artifact_name>
```

Maven Non-Interactive mode creates a project with the name that you passed in <custom_articatId_name> under <custom_groupId_name>.

or

```bash
mvn archetype:generate -DarchetypeGroupId=com.deliveredtechnologies -DarchetypeArtifactId="tf-s3-archetype" -DarchetypeVersion=0.12
``` 

After running the above command mvn interactive console prompts for the required arguments (ex: groupId and artifactId) and creates the project accordingly.

### How to Use Terraform Maven Projects

If you used the above configuration, the following Terraform Maven goals are mapped to the project's Maven phases.

| Maven Phase | Terraform Maven Goals |
|-------------|-----------------------|
| install     | deploy                |
| validate    | get
| clean       | init, destroy, clean  |
| package     | package               |
| deploy      | deploy                |

Use the plugin or update the plugin configuration in the POM to call terraform specific commands (e.g. _mvn tf:init tf:apply_).

### Articles

* [Making Terraform Testing Groovy: Part 1 - Pre-provision Testing](https://medium.com/deliveredtechnologies/making-terraform-testing-groovy-6a9278bdce1)
* [Unit Testing Terraform](https://medium.com/deliveredtechnologies/unit-testing-terraform-e592a5c3777f)
* [Maven Gives Terraform a Big Boost](https://dzone.com/articles/maven-gives-terraform-a-big-boost)
