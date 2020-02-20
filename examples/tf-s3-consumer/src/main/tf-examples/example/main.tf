module "s3" {
  source = "../../tf/root"
  bucket_name = var.bucket_name
  environment = var.environment
  region = var.region
}

