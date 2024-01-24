package com.example.finalproject.Models;

import java.util.List;

public interface OnDeleteDataListener {
    void onDeleteSuccess();

    void onDeleteFailure(String errorMessage);
}
