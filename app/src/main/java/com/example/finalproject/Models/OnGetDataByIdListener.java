package com.example.finalproject.Models;

import java.util.List;

public interface OnGetDataByIdListener {
    void onDataListLoaded(List<Product> dataList);

    void onDataListError(String errorMessage);
}
