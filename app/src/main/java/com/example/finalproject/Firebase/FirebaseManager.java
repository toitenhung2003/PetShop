package com.example.finalproject.Firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.finalproject.Models.Cart;
import com.example.finalproject.Models.OnDeleteDataListener;
import com.example.finalproject.Models.OnGetDataByIdListener;
import com.example.finalproject.Models.OnGetDataListener;
import com.example.finalproject.Models.OnUpdateDataListener;
import com.example.finalproject.Models.OnWriteDataListener;
import com.example.finalproject.Models.Order;
import com.example.finalproject.Models.Product;
import com.example.finalproject.Utils.DialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String DATABASE_NAME = "FinalProject";

    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;
    private static FirebaseFirestore mFireStore;
    private static CollectionReference productsRef;
    private static CollectionReference orderRef;

    public static void init() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFireStore = FirebaseFirestore.getInstance();
        productsRef = mFireStore.collection("products");
        orderRef = mFireStore.collection("orders");
    }

    public static FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public static DatabaseReference getDatabaseReference() {
        return mDatabase;
    }

    // Đăng ký người dùng
    public static void signUpUser(Context context, String email, String password, OnSignUpListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener.onSignUpSuccess(user);
                        } else {
                            listener.onSignUpFailure(
                                    "Unable to retrieve user information after registration.");
                        }
                    } else {
                        // Đăng ký thất bại
                        listener.onSignUpFailure(task.getException().getMessage());
                    }
                });
    }

    // Phương thức đăng nhập người dùng
    public static void signInUser(Context context, String email, String password, OnSignInListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener.onSignInSuccess(user);
                        } else {
                            listener.onSignInFailure("Unable to retrieve user information after login.");
                        }
                    } else {
                        // Đăng nhập thất bại
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            // Email không tồn tại trong hệ thống
                            listener.onSignInFailure("Email does not exist");
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            // Sai mật khẩu
                            listener.onSignInFailure("Incorrect password.");
                        } else {
                            // Xử lý các trường hợp khác
                            listener.onSignInFailure(exception.getMessage());
                        }
                    }
                });
    }

    public static void verifyEmail(FirebaseUser user, OnVerifyEmailListener listener) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onVerifySuccess(user);
            } else {
                listener.onVerifyFailure(task.getException().getMessage());
            }
        });
    }

    public static void addNewProduct(Product product, OnWriteDataListener listener) {
        String productId = productsRef.document().getId();
        product.setId(productId);
        productsRef.document().set(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onWriteSuccess();
            } else {
                listener.onWriteFailure(task.getException().getMessage());
            }
        });
    }

    public static void getAllData(OnGetDataListener listener) {
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<Product> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Product product = new Product();
                        product.setId(document.getId());
                        product.setName(document.getString("name"));
                        product.setPrice(document.getLong("price"));
                        product.setDesc(document.getString("desc"));
                        product.setInventory(document.getLong("inventory"));
                        product.setImage(document.getString("image"));

                        list.add(product);
                    }
                    listener.onDataListLoaded(list);
                } else {
                    listener.onDataListError("QuerySnapshot is null");
                }
            } else {
                listener.onDataListError(task.getException().getMessage());
            }
        });
    }

    public static void getProductsByIds(List<String> productIds, OnGetDataByIdListener listener) {
        productsRef.whereIn(FieldPath.documentId(), productIds).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<Product> list = new ArrayList<>();
                    for (String productId : productIds) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            if (document.getId().equals(productId)) {
                                Product product = document.toObject(Product.class);
                                list.add(product);
                                break;
                            }
                        }
                    }
                    listener.onDataListLoaded(list);
                } else {
                    listener.onDataListError("QuerySnapshot is null");
                }
            } else {
                listener.onDataListError(task.getException().getMessage());
            }
        });
    }

    public static void updateProduct(Product product, OnUpdateDataListener listenerUpdate) {
        DocumentReference documentReference = productsRef.document(product.getId());
        documentReference.set(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listenerUpdate.onUpdateSuccess();
            } else {
                listenerUpdate.onUpdateFailure(task.getException().getMessage());
            }
        });
    }

    public static void deleteProduct(String id, OnDeleteDataListener listenerDelete) {
        DocumentReference documentReference = productsRef.document(id);
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listenerDelete.onDeleteSuccess();
            } else {
                listenerDelete.onDeleteFailure(task.getException().getMessage());
            }
        });
    }

    public static void addNewOrder(Order order, OnWriteDataListener listener) {
        String orderId = orderRef.document().getId();
        order.setId(orderId);
        orderRef.document().set(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onWriteSuccess();
            } else {
                listener.onWriteFailure(task.getException().getMessage());
            }
        });
    }

    public static void updateInventory(String id, long inventory, OnUpdateDataListener listener) {
        productsRef.document(id).update("inventory", inventory)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onUpdateSuccess();
                    } else {
                        listener.onUpdateFailure(task.getException().getMessage());
                    }
                });
    }

}
