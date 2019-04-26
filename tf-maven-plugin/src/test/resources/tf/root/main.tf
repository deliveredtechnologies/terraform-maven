#An empty main.tf for testing

module "test-module" {
  source = "../../tfmodules/test-module"
  something = "test"
}
