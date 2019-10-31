output "bucket_arn" {
  description = "The arn of the S3 bucket"
  value = "${module.s3.bucket_arn}"
}
