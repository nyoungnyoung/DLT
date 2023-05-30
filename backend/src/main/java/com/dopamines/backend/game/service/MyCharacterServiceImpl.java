package com.dopamines.backend.game.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.game.dto.MyCharacterDto;
import com.dopamines.backend.game.entity.Inventory;
import com.dopamines.backend.game.entity.Item;
import com.dopamines.backend.game.entity.MyCharacter;
import com.dopamines.backend.game.repository.InventoryRepository;
import com.dopamines.backend.game.repository.ItemRepository;
import com.dopamines.backend.game.repository.MyCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MyCharacterServiceImpl implements MyCharacterService {
    private final MyCharacterRepository myCharacterRepository;
    private final AccountRepository accountRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public MyCharacterDto getMyCharacter(String email){
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()){
            log.info("CharacterServiceImpl의 getMyCharacter에서 account.isEmpty()");
            return null;
        } else{
            MyCharacter myCharacter = myCharacterRepository.findByAccount(account.get());
            log.info("eye" +myCharacter.getEye());
            log.info("tail" +myCharacter.getTail());
            log.info("body" +myCharacter.getBody());
            log.info("bodyPart" +myCharacter.getBodyPart());
            log.info("gloves" +myCharacter.getGloves());
            log.info("mouth&nose" +myCharacter.getMouthAndNose());
            MyCharacterDto myCharacterDto = new MyCharacterDto();

            Optional<Item> body = itemRepository.findByItemId(myCharacter.getBody());
            Optional<Item> bodyPart = itemRepository.findByItemId(myCharacter.getBodyPart());
            Optional<Item> eye = itemRepository.findByItemId(myCharacter.getEye());
            Optional<Item> gloves = itemRepository.findByItemId(myCharacter.getGloves());
            Optional<Item> mouthAndNose = itemRepository.findByItemId(myCharacter.getMouthAndNose());
            Optional<Item> tail = itemRepository.findByItemId(myCharacter.getTail());

            if(body.isEmpty()) {
                myCharacterDto.setBodies(0);
            } else {
                myCharacterDto.setBodies(body.get().getCode());
            }

            if(bodyPart.isEmpty()) {
                myCharacterDto.setBodyParts(0);
            } else {
                myCharacterDto.setBodyParts(bodyPart.get().getCode());
            }

            if(eye.isEmpty()) {
                myCharacterDto.setEyes(0);
            } else {
                myCharacterDto.setEyes(eye.get().getCode());
            }

            if(gloves.isEmpty()) {
                myCharacterDto.setGloves(0);
            } else {
                myCharacterDto.setGloves(gloves.get().getCode());
            }

            if(mouthAndNose.isEmpty()) {
                myCharacterDto.setMouthAndNoses(0);
            } else {
                myCharacterDto.setMouthAndNoses(mouthAndNose.get().getCode());
            }

            if(tail.isEmpty()) {
                myCharacterDto.setTails(0);
            } else {
                myCharacterDto.setTails(tail.get().getCode());
            }



            return myCharacterDto;

        }
    }

    @Override
    public void wearItem(String email, MyCharacterDto myCharacterDto){
        Optional<Account> account = accountRepository.findByEmail(email);
        log.info("myCharacterDto.getBodies(): " + myCharacterDto.getBodies());
        log.info("myCharacterDto.getBodyParts(): " + myCharacterDto.getBodyParts());
        log.info("myCharacterDto.getEyes(): " + myCharacterDto.getEyes());
        log.info("myCharacterDto.getGloves(): " + myCharacterDto.getGloves());
        log.info("myCharacterDto.getMouthAndNoses(): " + myCharacterDto.getMouthAndNoses());
        log.info("myCharacterDto.getTails(): " + myCharacterDto.getTails());

        // 내 인벤토리 가져오기
        List<Inventory> inventoryList = inventoryRepository.findAllByAccount(account.get());
        for(Inventory i : inventoryList) {
            log.info("inventoryList: " + i.getItem().getCategory()+ "/" + i.getItem().getItemId());
        }

        // 인벤토리에서 itemId값만 가져오기
        List<Integer> itemIdList = inventoryListToItemIdList(inventoryList);
        for(Integer i : itemIdList) {
            log.info("itemIdList: " + i);
        }

        // 현재 내 캐릭터 착장 정보 DB에서 불러오기
        MyCharacter myCharacter = myCharacterRepository.findMyCharacterByAccount_Email(email);

        // 사용자가 요청한 착장 정보 Item Entity로 불러오기
        Optional<Item> body = itemRepository.findByCategoryAndCode("bodies", myCharacterDto.getBodies());
        Optional<Item> bodyPart = itemRepository.findByCategoryAndCode("body_parts", myCharacterDto.getBodyParts());
        Optional<Item> eye = itemRepository.findByCategoryAndCode("eyes", myCharacterDto.getEyes());
        Optional<Item> gloves = itemRepository.findByCategoryAndCode("gloves", myCharacterDto.getGloves());
        Optional<Item> mouthAndNose = itemRepository.findByCategoryAndCode("mouth_and_noses", myCharacterDto.getMouthAndNoses());
        Optional<Item> tail = itemRepository.findByCategoryAndCode("tails", myCharacterDto.getTails());

        // body check : 0 값은 불가능
        if (body.isEmpty()) {
            throw new IllegalArgumentException("body 해당 아이템이 없습니다.");
        } else {
            
            // 아니면 요청한 body 소유 여부 확인 후 body
            if(itemIdList.contains(body.get().getItemId())){
                myCharacter.setBody(body.get().getItemId());
            } else {
                throw new IllegalArgumentException("body 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }

        // bodyPart check, inventory에 있는지 확인
        if (myCharacterDto.getBodyParts()!=0 && bodyPart.isEmpty()) {
            throw new IllegalArgumentException("bodyPart 해당 아이템이 없습니다.");
        } else {

            // 위 조건을 지나쳐서 왔는데 bodyPart.isEmpty() == true 면 아무것도 장착하지 않았다는 뜻
            if(bodyPart.isEmpty()) {
                // 저장할 때 0 넣어서 저장해준다.
                myCharacter.setBodyPart(0);
            // 그게 아니면 요청한 코드로 담아서 저장해줌
            }else if(itemIdList.contains(bodyPart.get().getItemId())){
                myCharacter.setBodyPart(bodyPart.get().getItemId());
            } else {
                throw new IllegalArgumentException("bodyPart 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }

        // eyes check, inventory에 있는지 확인
        if (myCharacterDto.getEyes()!=0 && eye.isEmpty()) {
            throw new IllegalArgumentException("eye 해당 아이템이 없습니다.");
        } else {

            if(eye.isEmpty()) {
                myCharacter.setEye(0);
            }else if(itemIdList.contains(eye.get().getItemId())){
                myCharacter.setEye(eye.get().getItemId());
            } else {
                throw new IllegalArgumentException("eye 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }

        // gloves check
        if (myCharacterDto.getGloves()!=0 && gloves.isEmpty()) {
            throw new IllegalArgumentException("gloves 해당 아이템이 없습니다.");
        } else {

            if(gloves.isEmpty()) {
                myCharacter.setGloves(0);
            }else if(itemIdList.contains(gloves.get().getItemId())){
                myCharacter.setGloves(gloves.get().getItemId());
            } else {
                throw new IllegalArgumentException("gloves 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }

        // mouthAndNose check
        if (myCharacterDto.getMouthAndNoses()!=0 && mouthAndNose.isEmpty()) {
            throw new IllegalArgumentException("mouthAndNose 해당 아이템이 없습니다.");
        } else {

            if(mouthAndNose.isEmpty()) {
                myCharacter.setMouthAndNose(0);
            }else if(itemIdList.contains(mouthAndNose.get().getItemId())){
                myCharacter.setMouthAndNose(mouthAndNose.get().getItemId());
            } else {
                throw new IllegalArgumentException("mouthAndNose 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }

        // tails check
        if (myCharacterDto.getTails()!=0 && tail.isEmpty()) {
            throw new IllegalArgumentException("tail 해당 아이템이 없습니다.");
        } else {

            if(tail.isEmpty()) {
                myCharacter.setTail(0);
            }else if(itemIdList.contains(tail.get().getItemId())){
                myCharacter.setTail(tail.get().getItemId());
            } else {
                throw new IllegalArgumentException("tail 구매하지 않은 아이템이 포함되어 있습니다.");
            }
        }
        myCharacterRepository.save(myCharacter);
    }

    private List<Integer> inventoryListToItemIdList(List<Inventory> inventoryList) {
        List<Integer> itemIdList = new ArrayList<Integer>();
        for(Inventory inventory : inventoryList) {
            itemIdList.add(inventory.getItem().getItemId());
        }
        return itemIdList;
    }
}
