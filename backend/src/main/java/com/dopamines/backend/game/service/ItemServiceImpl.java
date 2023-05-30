package com.dopamines.backend.game.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.game.dto.ItemDto;
import com.dopamines.backend.game.dto.ShopResponseDto;
import com.dopamines.backend.game.entity.Inventory;
import com.dopamines.backend.game.entity.Item;
import com.dopamines.backend.game.entity.MyCharacter;
import com.dopamines.backend.game.repository.InventoryRepository;
import com.dopamines.backend.game.repository.ItemRepository;
import com.dopamines.backend.game.repository.MyCharacterRepository;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final MyCharacterRepository myCharacterRepository;
    private final AccountRepository accountRepository;
    private final MyCharacterService myCharacterService;


    @Override
    public HashMap<String, List<ItemDto>> getItems(String email){

        HashMap<String, List<ItemDto>> res = new HashMap<>();

        res.put("Eyes", toItemList(itemRepository.findByCategory("eyes"), email));
        res.put("Bodies", toItemList(itemRepository.findByCategory("bodies"), email));
        res.put("BodyParts", toItemList(itemRepository.findByCategory("body_parts"), email));
        res.put("MouthAndNoses", toItemList(itemRepository.findByCategory("mouth_and_noses"), email));
        res.put("Gloves", toItemList(itemRepository.findByCategory("gloves"), email));
        res.put("Tails", toItemList(itemRepository.findByCategory("tails"), email));

        return res;
    }

    // ItemList를 ItemDtoList로 바꿈
    private List<ItemDto> toItemList(List<Item> items, String email) {
        List<Inventory> inventory = inventoryRepository.findAllByAccount_Email(email);
//        log.info("toItemList의 inventory: "+ inventory.get(0).toString());
        List<Integer> itemIdList = inventoryListToItemIdList(inventory);
        MyCharacter myCharacter = myCharacterRepository.findMyCharacterByAccount_Email(email);
        List<ItemDto> list = new ArrayList<ItemDto>();
        for(Item item : items){
            ItemDto itemDto = new ItemDto();
            itemDto.setCode(item.getCode());
            itemDto.setPrice(item.getPrice());
            itemDto.setItemId(item.getItemId());

            if(itemIdList.contains(item.getItemId())){
                itemDto.setBought(true);
            } else {
                itemDto.setBought(false);
            }

            if(item.getCategory().equals("bodies")){
                if (item.getItemId() == myCharacter.getBody()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else if(item.getCategory().equals("body_parts")){
                if (item.getItemId() == myCharacter.getBodyPart()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else if(item.getCategory().equals("eyes")){
                if (item.getItemId() == myCharacter.getEye()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else if(item.getCategory().equals("gloves")){
                if (item.getItemId() == myCharacter.getGloves()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else if(item.getCategory().equals("mouth_and_noses")){
                if (item.getItemId() == myCharacter.getMouthAndNose()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else if(item.getCategory().equals("tails")){
                if (item.getItemId() == myCharacter.getTail()) {
                    itemDto.setWorn(true);
                } else {
                    itemDto.setWorn(false);
                }
            } else {
                log.info("category가 이상한데.. " + item.getCategory());
            }

            list.add(itemDto);
        }
        return list;
    }

    @Override
    public void buyItem(String email, int itemId) {
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        List<Inventory> inventories = inventoryRepository.findAllByAccount(account.get());
        Optional<Item> item = itemRepository.findById(itemId);

        if(item.isEmpty()) {
            throw new RuntimeException("해당 아이템이 존재하지 않습니다.");
        }

        if(inventoryListToItemIdList(inventories).contains(item.get().getItemId())) {
            throw new RuntimeException("이미 구매한 상품입니다.");
        }

        int thyme = account.get().getThyme();
        int price = item.get().getPrice();

        // 충분한 thyme이 있을 때
        if (thyme - price >= 0) {
            account.get().setThyme(thyme - price);
            // 인벤토리 추가
            Inventory inventory = new Inventory();
            inventory.setItem(item.get());
            inventory.setAccount(account.get());
            inventoryRepository.save(inventory);

        } else {
            throw new RuntimeException("해당 아이템을 구매하기엔 thyme이 모자랍니다.");
        }

    }

    @Override
    public ShopResponseDto getShop(String email) {
        Account account = new Account();
        HashMap<String, List<ItemDto>> items = getItems(email);
        ShopResponseDto shopResponseDto = new ShopResponseDto();
        shopResponseDto.setThyme(accountRepository.findByEmail(email).get().getThyme());
        shopResponseDto.setMyCharacter(myCharacterService.getMyCharacter(email));
        shopResponseDto.setItems(items);

        return shopResponseDto;
    };

    @Override
    public List<Integer> inventoryListToItemIdList(List<Inventory> inventoryList) {
        List<Integer> itemIdList = new ArrayList<Integer>();
        for(Inventory inventory : inventoryList) {
            itemIdList.add(inventory.getItem().getItemId());
        }
        return itemIdList;
    }


}
