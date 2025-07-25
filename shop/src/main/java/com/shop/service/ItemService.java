package com.shop.service;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // 1. 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 2. 이미지 등록 (업로드된 것만)
        int uploadedImageIndex = 0;
        for (MultipartFile file : itemImgFileList) {

            // 파일이 비어있으면 skip
            if (file == null || file.isEmpty()) {
                continue;
            }

            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            // 첫 번째 업로드된 이미지만 대표 이미지로 지정
            itemImg.setRepimgYn(uploadedImageIndex == 0 ? "Y" : "N");
            uploadedImageIndex++;

            itemImgService.saveItemImg(itemImg, file);
        }

        return item.getId();
    }


    @Transactional(readOnly = true) //상품데이터 읽어오는 트랜잭션 읽기전용으로 설정. JPA 변경감지 수행하지 않아 성능향상
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        // 실제 저장된 이미지 DTO 변환
        for (ItemImg itemImg : itemImgList) {
            itemImgDtoList.add(ItemImgDto.of(itemImg));
        }

        // 총 5개 슬롯 맞춰주기 (프론트에서 빈칸 보여주기 위함)
        while (itemImgDtoList.size() < 5) {
            itemImgDtoList.add(new ItemImgDto());
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile file = itemImgFileList.get(i);

            // 파일이 비어있으면 기존 이미지 그대로 유지
            if (file == null || file.isEmpty()) {
                continue;
            }

            itemImgService.updateItemImg(itemImgIds.get(i), file);
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    //메인 페이지에 보여줄 상품 데이터 조회하는 메소드
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
