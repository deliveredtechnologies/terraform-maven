package com.deliveredtechnologies.terraform;

public enum TerraformCommand {
  INIT("init"), PLAN("plan"), APPLY("apply"), DESTROY("destroy"), OUT("out");

  private String value;

  private TerraformCommand(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
