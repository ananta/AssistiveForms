package com.anntz.formservice.repository;

import com.anntz.formservice.model.FormItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormItemRepository extends JpaRepository<FormItem, Long> {
    List<FormItem> findByFormId(long formId);

    @Transactional
    void deleteByFormId(long tutorialId);
}
