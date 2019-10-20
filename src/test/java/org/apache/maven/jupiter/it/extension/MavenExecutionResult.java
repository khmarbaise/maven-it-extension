package org.apache.maven.jupiter.it.extension;

public class MavenExecutionResult {

  private final ExecutionResult result;

  private final int returnCode;

  public MavenExecutionResult(ExecutionResult result, int returnCode) {
    this.result = result;
    this.returnCode = returnCode;
  }

  public int getReturnCode() {
    return returnCode;
  }

  public boolean isSuccesful() {
    return ExecutionResult.Successful.equals(this.result);
  }

  public boolean isFailure() {
    return ExecutionResult.Failure.equals(this.result);
  }

  public boolean isError() {
    return ExecutionResult.Error.equals(this.result);
  }

  public ExecutionResult getResult() {
    return this.result;
  }

  public enum ExecutionResult {
    Successful,
    Failure,
    Error
  }
}
