package cardreader.sunplus.com.cardreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cardreader.sunplus.com.cardreader.utils.AppConstants;

public class MyBottomSheet extends BottomSheetDialogFragment {

    AppCompatTextView name, phoneNumber, email;
    private String nameVal, emailVal, phNoVal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            nameVal = getArguments().getString(AppConstants.KEY_NAME);
            emailVal = getArguments().getString(AppConstants.KEY_EMAIL_NUMBER);
            phNoVal = getArguments().getString(AppConstants.KEY_PHONE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_bottom_sheet, container, false);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phno);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name.setText(nameVal);
        email.setText(emailVal);
        phoneNumber.setText(phNoVal);

    }
}
