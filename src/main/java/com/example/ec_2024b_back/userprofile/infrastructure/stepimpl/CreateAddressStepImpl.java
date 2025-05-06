package com.example.ec_2024b_back.userprofile.infrastructure.stepimpl;

import com.example.ec_2024b_back.share.domain.services.IdGenerator;
import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow;
import com.example.ec_2024b_back.userprofile.application.workflow.AddAddressWorkflow.CreateAddressStep;
import com.example.ec_2024b_back.userprofile.domain.models.Address;
import com.example.ec_2024b_back.userprofile.domain.models.AddressId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** 住所作成ステップの実装 */
@Component
@Primary
@RequiredArgsConstructor
public class CreateAddressStepImpl implements CreateAddressStep {

  private final IdGenerator idGenerator;

  @Override
  public AddAddressWorkflow.Context.AddressCreated apply(AddAddressWorkflow.Context.Found found) {
    // IDを生成して新しい住所を作成
    var address =
        new Address(
            new AddressId(idGenerator.newId()),
            found.name(),
            found.postalCode(),
            found.prefecture(),
            found.city(),
            found.street(),
            found.building(),
            found.phoneNumber(),
            found.isDefault());

    return new AddAddressWorkflow.Context.AddressCreated(found.userProfile(), address);
  }
}
