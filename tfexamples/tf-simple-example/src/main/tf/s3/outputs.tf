output "bucket_arns" {
  description = "a list of bucket arns: [bucket_arn] for non-replicated; [source_arn, destination_arn] for replicated"
  value = "${var.is_replicated ? list(join("", list(aws_s3_bucket.source_bucket.arn)), aws_s3_bucket.replication_bucket.arn) : list("", join(aws_s3_bucket.bucket.arn))}"
}
