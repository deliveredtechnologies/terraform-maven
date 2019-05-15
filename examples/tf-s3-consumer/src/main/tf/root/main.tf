locals {
  bucket_arns = "${split(",", module.s3.bucket_arns)}"
}

module "s3" {
  source = "../../.tfmodules/s3"
  bucket_name = "${var.bucket_name}"
  environment = "${var.environment}"
  region = "${var.region}"
  replication_region = "${var.replication_region}"
  is_replicated = true
}
