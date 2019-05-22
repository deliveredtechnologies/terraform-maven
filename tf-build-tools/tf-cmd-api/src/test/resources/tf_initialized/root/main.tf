#An empty main.terraform for testing

module "test-module" {
  source = "../../tfmodules/test-module"
  something = "test"
}
