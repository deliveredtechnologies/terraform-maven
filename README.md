![terraform-maven](.docs/MavenTerraform.png)

---

[![Build Status](https://travis-ci.org/deliveredtechnologies/terraform-maven.svg?branch=develop&maxAge=600)](https://travis-ci.org/deliveredtechnologies/terraform-maven)

# Terraform Maven Plugin

### Goals

**tf:get**

Description:

Downloads Maven artifacts into a common modules directory and extracts each artifacts
contents into a folder named for the artifact (version agnostic). The default location
of the modules folder is `src/main/.tfModules`.

Optional Parameters:

| Name      | Type   | Description                                                     |
| --------- | ------ | --------------------------------------------------------------- |
| tfModules | String | The directory location where Terraform modules will be expanded |

**tf:init**

Description:

Executes the `terraform init` command. See [https://www.terraform.io/docs/commands/init.html](https://www.terraform.io/docs/commands/init.html).

Optional Parameters:

| Name      | Type   | Description                                                            |
| --------- | ------ | ---------------------------------------------------------------------- |
| tfRootDir | String | The root module directory location where terraform will be initialized |

