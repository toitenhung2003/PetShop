package com.example.finalproject.Models;

public interface OnWriteDataListener {
    void onWriteSuccess();

    void onWriteFailure(String errorMessage);
}
