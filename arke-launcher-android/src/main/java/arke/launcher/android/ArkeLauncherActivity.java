package arke.launcher.android;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import arke.container.jpa.messenger.sms.android.AndroidIntentDeliveryHandler;
import arke.container.jpa.messenger.sms.android.AndroidIntentMessenger;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ArkeLauncherActivity extends Activity {

    private static final Logger LOG = Logger.getLogger(ArkeLauncherActivity.class.getName());

    public static final String PREFERENCE_KEY_ADDRESS = "address";

    private static class Message {
        private boolean inbound;
        private String address;
        private String message;

        public Message(String address, String message, boolean inbound) {
            this.inbound = inbound;
            this.address = address;
            this.message = message;
        }

        public boolean isInbound() {
            return inbound;
        }

        public String getAddress() {
            return address;
        }

        public String getMessage() {
            return message;
        }
    }

    private class MessageAdapter extends BaseAdapter {

        private ArrayList<Message> messages;

        public MessageAdapter() {
            this.messages = new ArrayList<Message>();
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if( convertView != null ) {
                view = convertView;
            } else {
                view = ArkeLauncherActivity.this.getLayoutInflater().inflate(R.layout.message, parent, false);
            }
            TextView addressView = (TextView)view.findViewById(R.id.message_address);
            TextView messageView = (TextView)view.findViewById(R.id.message_body);
            TextView directionView = (TextView)view.findViewById(R.id.message_direction);

            Message message = messages.get(position);

            addressView.setText(message.getAddress());
            messageView.setText(message.getMessage());
            if( message.isInbound() ) {
                directionView.setText(R.string.message_inbound);
            } else {
                directionView.setText(R.string.message_outbound);
            }
            return view;
        }

        public void addMessage(Message message) {
            this.messages.add(message);
            this.notifyDataSetChanged();
        }

        public String getAddress(int position) {
            return this.messages.get(position).getAddress();
        }

    }

    private BroadcastReceiver messageReceiver;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                eatIntent(intent);
            }
        };
        this.messageAdapter = new MessageAdapter();

        // set up the UI
        setContentView(R.layout.main);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        eatIntent(intent);

    }

    private void eatIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if( bundle != null ) {
            String message = bundle.getString(AndroidIntentMessenger.FIELD_MESSAGE);
            String to = bundle.getString(AndroidIntentMessenger.FIELD_TO);
            LOG.info("received message "+message+" to "+to);

            Message m = new Message(to, message, true);
            this.messageAdapter.addMessage(m);
            ListView listView = (ListView)findViewById(R.id.messages);
            listView.setSelection(messageAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final TextView serviceNameView = (TextView)findViewById(R.id.service_name);
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        String address = preferences.getString(PREFERENCE_KEY_ADDRESS, getString(R.string.service_default));
        serviceNameView.setText(address);

        final View messagePanel = findViewById(R.id.message_panel);
        final View startButton = findViewById(R.id.service_start_button);
        final View stopButton = findViewById(R.id.service_stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( startService(serviceNameView.getText().toString()) ) {
                    serviceNameView.setEnabled(false);
                    messagePanel.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceNameView.getText().toString());
                serviceNameView.setEnabled(true);
                messagePanel.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
            }
        });

        final TextView messageView = (TextView)findViewById(R.id.message);
        final TextView addressView = (TextView)findViewById(R.id.address);
        final TextView addressTypeView = (TextView)findViewById(R.id.address_type);
        addressTypeView.setText(getClass().getName());

        final View sendButton = findViewById(R.id.message_button);

        final ListView listView = (ListView)findViewById(R.id.messages);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AndroidIntentMessenger.toIntent(serviceNameView.getText().toString());

                String address = addressView.getText().toString();
                String message = messageView.getText().toString();
                String addressType = addressTypeView.getText().toString();

                LOG.info("Sending "+message+" to "+address+" of type "+addressType);

                intent.putExtra(AndroidIntentDeliveryHandler.FIELD_ADDRESS, address);
                intent.putExtra(AndroidIntentDeliveryHandler.FIELD_MESSAGE, message);
                intent.putExtra(AndroidIntentDeliveryHandler.FIELD_TYPE, addressType);

                // should just be delivered as per normal
                startService(intent);

                Message m = new Message(address, message, false);
                messageAdapter.addMessage(m);
                listView.setSelection(messageAdapter.getCount() - 1);

                messageView.setText("");
            }
        });

        listView.setAdapter(messageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = messageAdapter.getAddress(position);
                addressView.setText(address);
            }
        });

        registerReceiver(this.messageReceiver, new IntentFilter(Intent.ACTION_MAIN));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(this.messageReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean startService(String address) {

        Log.d(getClass().getName(), "staring "+address);

        // remember the service name
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_KEY_ADDRESS, address);
        editor.commit();

        Intent intent = AndroidIntentMessenger.toIntent(address);
        ComponentName componentName = startService(intent);
        // do nothing with this?
        if( componentName != null ) {
            Log.d(getClass().getName(), componentName.flattenToString());
        } else {
            Log.e(getClass().getName(), "no component found for "+address);
        }
        return componentName != null;

    }

    private boolean stopService(String address) {
        Log.d(getClass().getName(), "stopping "+address);

        Intent intent = AndroidIntentMessenger.toIntent(address);
        boolean worked = this.stopService(intent);
        if( worked ) {
            Log.d(getClass().getName(), "stopped " + address);
        } else {
            Log.d(getClass().getName(), "failed to stop "+address);
        }
        return worked;
    }

}
