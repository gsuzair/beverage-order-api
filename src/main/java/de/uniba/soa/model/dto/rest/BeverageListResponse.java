package de.uniba.soa.model.dto.rest;

import java.util.List;

public class BeverageListResponse {

    private Pagination pagination;
    private List<BeverageDTO> beverages;
    private String href;

    public BeverageListResponse() {}

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<BeverageDTO> getBeverages() {
        return beverages;
    }

    public void setBeverages(List<BeverageDTO> beverages) {
        this.beverages = beverages;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
