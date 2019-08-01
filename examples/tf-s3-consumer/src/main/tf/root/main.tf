locals {
  bucket_arns = "${split(",", module.s3.bucket_arn)}"
}

module "s3" {
  source = "../../.tfmodules/s3/s3"
  bucket_name = "${var.bucket_name}"
  environment = "${var.environment}"
  region = "${var.region}"
}
