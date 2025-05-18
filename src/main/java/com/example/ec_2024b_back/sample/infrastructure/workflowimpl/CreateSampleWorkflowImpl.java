package com.example.ec_2024b_back.sample.infrastructure.workflowimpl;

import com.example.ec_2024b_back.sample.application.workflow.CreateSampleWorkflow;
import org.springframework.stereotype.Component;

/**
 * サンプル作成ワークフローの実装。
 *
 * <p>このクラスは、ワークフローの具体的な実装と依存性注入の例を示します。
 */
@Component
public class CreateSampleWorkflowImpl extends CreateSampleWorkflow {

  public CreateSampleWorkflowImpl(
      ValidateInputStep validateInputStep,
      CreateSampleStep createSampleStep,
      SaveSampleStep saveSampleStep) {
    super(validateInputStep, createSampleStep, saveSampleStep);
  }
}
