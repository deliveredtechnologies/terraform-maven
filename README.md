![terraform-maven](.docs/MavenTerraform.png)

---

[![Build Status](https://travis-ci.org/deliveredtechnologies/terraform-maven.svg?branch=develop&maxAge=600&service=github)](https://travis-ci.org/deliveredtechnologies/terraform-maven)
[![Coverage Status](https://coveralls.io/repos/github/deliveredtechnologies/terraform-maven/badge.svg?branch=develop&maxAge=600&service=github)](https://coveralls.io/github/deliveredtechnologies/terraform-maven?branch=develop)

# Terraform Maven Plugin

The Terraform Maven Plugin brings Terraform into Maven and greatly enhances the Terraform code management
lifecycle and management experience. Maven in some form has been the standard for Java project management for over a decade.
Now, all of that Maven goodness can be used with Terraform.

### Contents

* [Benefits of the Terraform Maven Plugin](#benefits-of-the-terraform-maven-plugin)
* [Maven Goals](#maven-goals)
  * [tf:get](#tfget)
  * [tf:init](#tfinit)
  * [tf:apply](#tfapply)
  * [tf:package](#tfpackage)

### Benefits of the Terraform Maven Plugin
* Dependency Management
  * Terraform really has no dependency management to speak of. Even when you use Terraform Enterprise or some other private
    Terraform module registry, a version update to a module means editing the Terraform code in each and every place
    that module is sourced. The Terraform Maven Plugin allows you to specify your dependencies for Terraform the same way
    you specify your dependencies with Java: in an external [Maven POM file](https://maven.apache.org/pom.html). And
    because it's Maven, Terraform modules sourced from Maven repos can also take advantage of version ranges and 
    Maven's SNAPSHOT functionality. No more modifying code for version updates! No more sourcing obscure URLs!
    Hooray, Maven! 
* Packaging as Part of the Build Lifecycle
  * The [package goal](#tfpackage) can package a Terraform root module for deployment into a Maven repo
    or it can package that same Terraform root module with its dependencies for deployment into an isolated environemnt
    or Terraform Enterprise. It's all part of a single Maven goal/command.
* Simple Integration with CI Tools
  * Get rid of hundreds of lines of untested code in your CI tool and replace it with tested build lifecycle management
    using Maven! Most CI tools either have Maven included or have a Maven plugin available. Less Terraform build logic 
    in your CI tool means more reliable builds and less CI code to maintain. 
    
### Maven Goals

#### tf:get

Description:

Downloads Maven artifacts into a common modules directory and extracts each artifacts
contents into a folder named for the artifact (version agnostic). The default location
of the modules folder is `src/main/.tfModules`.

Optional Parameters:

| Name         | Type   | Description                                                     |
| ------------ | ------ | --------------------------------------------------------------- |
| tfModulesDir | String | The directory location where Terraform modules will be expanded |

---

#### tf:init

Description:

Executes the `terraform init` command. See [https://www.terraform.io/docs/commands/init.html](https://www.terraform.io/docs/commands/init.html).

Optional Parameters:

| Name      | Type   | Description                                                            |
| --------- | ------ | ---------------------------------------------------------------------- |
| tfRootDir | String | The root module directory location where terraform will be initialized |

---

#### tf:apply

Description:

Executes the `terraform apply` command. See [https://www.terraform.io/docs/commands/apply.html](https://www.terraform.io/docs/commands/apply.html).

Optional Parameters:

| Name        | Type   | Description                                                                                 |
| ----------- | ------ | ------------------------------------------------------------------------------------------- |
| varFiles    | String | A comma delimited string of tfvars files                                                    |
| tfVars      | String | A comma delimited string of tfvars (e.g. -var 'name=value')                                 |
| lockTimeout | Number | Duration to retry a state lock                                                              |
| target      | Number | A Resource Address to target                                                                |
| target      | String | A Resource Address to target                                                                |
| autoApprove | Any    | If this property exists, the -auto-approve flag is set                                      |
| noColor     | Any    | If this property exists, the -no-color flag is set                                          |
| timeout     | Number | The maximum time in milliseconds that the terraform apply command can run; default is 10min |

---

#### tf:package

Description:

Recursively packages the Terraform files from the root module directory as target/{artifact-id}-{version}.zip.
Optionally, a fat zip can be created instead, which also includes the Terraform module dependencies; see the [tf:get goal](#tfget) above.

_Note: Within the fat zip, module source paths are updated accordingly so that the zip is a wholly contained working module that can be consumed as a module,
extracted, initialized and applied as-is or submitted to Terraform Enterprise._

| Name         | Type    | Description                                                                                   |
| ------------ | ------- | --------------------------------------------------------------------------------------------- |
| tfRootDir    | String  | The terraform root module directory location; defaults to src/main/tf/{first directory found} |
| tfModulesDir | String  | The directory that contains the Terraform module depenencies; defaults to src/main/.tfmodules |
| fatZip       | Boolean | Set to true if a fat zip should be created, otherwise false; defaults to false                |
