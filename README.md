![terraform-maven](.docs/MavenTerraform.png)

---

# Terraform Maven Plugin

### Goals

**tf:get**

Description:

Downloads Maven artifacts into a common modules directory and extracts each artifacts
contents into a folder named for the artifact (version agnostic). The default location
of the modules folder is `src/main/.tfModules`.

Optional Parameters:

| Name      | Type   | Description                                                       |
| --------- | ------ | ----------------------------------------------------------------- |
| tfModules | String | The directory location where Terraform modules should be expanded |

