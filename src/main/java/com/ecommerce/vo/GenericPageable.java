package com.ecommerce.vo;

import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public class GenericPageable { //頁碼相關資訊
	
	private int currentPageNo;//目前頁面 
	private int pageDataSize;//每頁面顯示的筆數  
	private int pagesIconSize;//顯示的分頁個數  
	private long dataTotalSize;//總筆數 
	private Set<Integer> pagination;//每次顯示當頁頁面的頁碼
	private long endPageNo;//最後一頁的頁碼 
	public int getCurrentPageNo() {
		return currentPageNo;
	}
	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}
	public int getPageDataSize() {
		return pageDataSize;
	}
	public void setPageDataSize(int pageDataSize) {
		this.pageDataSize = pageDataSize;
	}
	public int getPagesIconSize() {
		return pagesIconSize;
	}
	public void setPagesIconSize(int pagesIconSize) {
		this.pagesIconSize = pagesIconSize;
	}
	public long getDataTotalSize() {
		return dataTotalSize;
	}
	public void setDataTotalSize(long dataTotalSize) {
		this.dataTotalSize = dataTotalSize;
	}
	public Set<Integer> getPagination() {
		return pagination;
	}
	public void setPagination(Set<Integer> pagination) {
//		Set<Integer> number=new HashSet<>();
		int pagCal=0;
		if(currentPageNo <= getCurrentPageNo()) {//目前頁面不能超過總頁面
			// 若可以整除 代表商-1去計算前面的
			pagCal=currentPageNo % pagesIconSize ==0 ? (currentPageNo / pagesIconSize)-1 :(currentPageNo / pagesIconSize);
		}
		for(int i=1;i<= pagesIconSize ;i++) {
			//將顯示頁面值加入 (ex:3*0+1,3*0+2,3*0+3)
			int page=pagesIconSize * pagCal + i;
			//如果超出實際最末頁 就不給它顯示當頁下的頁面的頁碼
			if(page<=getEndPageNo()) {
				pagination.add(page);
//				number.add(page);
			}
		}
		this.pagination = pagination;
//		this.pagination = number;
	}
	public long getEndPageNo() {
		//總筆數%每頁筆數 若=0 總頁碼為商,無法整除 總頁碼為商+1
		this.endPageNo=dataTotalSize % pageDataSize==0 ? (dataTotalSize / pageDataSize) : (dataTotalSize / pageDataSize)+1 ;
		return endPageNo;
	}
	public void setEndPageNo(long endPageNo) {
		this.endPageNo = endPageNo;
	}
	
	
	
}
