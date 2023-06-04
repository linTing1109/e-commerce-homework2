package com.ecommerce.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.BeverageMember;

@Repository
public interface BeverageMemberDao extends JpaRepository<BeverageMember, String>{
	BeverageMember findByIdentificationNoAndPassword(String identificationNo, String password);
	BeverageMember findByIdentificationNo(String identificationNo);
}
