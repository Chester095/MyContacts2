package com.example.mycontacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import Data.ContactsAppDatabase;
import Model.Contact;

public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAppDatabase contactsAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        contactsAppDatabase = Room.databaseBuilder(getApplicationContext(), ContactsAppDatabase.class, "ContactsDB").build();

        new GetAllContactsAsyncTask().execute();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);


        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContacts(false, null, -1);
            }


        });
    }


    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newContactTitle = view.findViewById(R.id.newContactTitle);
        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText familyEditText = view.findViewById(R.id.familyEditText);
        final EditText emailEditText = view.findViewById(R.id.emailEditText);
        final EditText phoneEditText = view.findViewById(R.id.phoneEditText);

        newContactTitle.setText(!isUpdate ? "Add Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            nameEditText.setText(contact.getName());
            familyEditText.setText(contact.getFamily());
            emailEditText.setText(contact.getEmail());
            phoneEditText.setText(contact.getPhone());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(isUpdate ? "Delete" : "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                if (isUpdate) {

                                    deleteContact(contact, position);
                                } else {

                                    dialogBox.cancel();

                                }

                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(nameEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact name!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(familyEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact price!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(emailEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact email!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact phone number!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }


                if (isUpdate && contact != null) {

                    updateContact(nameEditText.getText().toString(), familyEditText.getText().toString(), emailEditText.getText().toString(),
                            phoneEditText.getText().toString(), position);
                } else {

                    createContact(nameEditText.getText().toString(), familyEditText.getText().toString(), emailEditText.getText().toString(),
                            phoneEditText.getText().toString());
                }
            }
        });
    }

    private void deleteContact(Contact contact, int position) {

        contactArrayList.remove(position);

        new DeleteContactAsyncTask().execute(contact);

    }

    private void updateContact(String name, String family, String email, String phone, int position) {

        Contact contact = contactArrayList.get(position);

        contact.setName(name);
        contact.setFamily(family);
        contact.setEmail(email);
        contact.setPhone(phone);

        new UpdateContactAsyncTask().execute(contact);

        contactArrayList.set(position, contact);


    }

    private void createContact(String name, String family, String email, String phone) {

        new CreateContactAsyncTask().execute(new Contact(0, name, family, email, phone));

    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            contactArrayList.addAll(contactsAppDatabase.getContactDAO().getAllContacts());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }
    }


    private class CreateContactAsyncTask extends AsyncTask<Contact, Void, Void> {


        @Override
        protected Void doInBackground(Contact... contacts) {

            long id = contactsAppDatabase.getContactDAO().addContact(
                    contacts[0]
            );


            Contact contact = contactsAppDatabase.getContactDAO().getContact(id);

            if (contact != null) {

                contactArrayList.add(0, contact);


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Contact... contacts) {

            contactsAppDatabase.getContactDAO().updateContact(contacts[0]);

            return null;
        }
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Contact... contacts) {

            contactsAppDatabase.getContactDAO().deleteContact(contacts[0]);


            return null;
        }
    }

}