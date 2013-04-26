package arke.sample.rps.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import arke.ContentTypeUtils;
import arke.container.jpa.DelegatingMessenger;
import arke.container.jpa.DeviceBasedTimeZoneGuesser;
import arke.container.jpa.JPAContainer;
import arke.container.jpa.Messenger;
import arke.container.jpa.data.*;
import arke.container.jpa.data.ormlite.*;
import arke.container.jpa.messenger.sms.android.*;
import arke.container.jpa.messenger.sms.android.data.SentMessage;
import arke.container.jpa.messenger.sms.android.data.SentMessagePart;
import arke.container.jpa.messenger.sms.android.data.ormlite.ORMLiteSentMessageDAO;
import arke.container.jpa.messenger.sms.android.data.ormlite.ORMLiteSentMessagePartDAO;
import arke.container.template.MessagePartTransformer;
import arke.container.template.MessagePartTransformerUniverseAdapter;
import arke.container.template.velocity.VelocityMessagePartTransformer;
import arke.sample.rps.RockPaperScissorsUniverse;
import arke.sample.rps.data.Action;
import arke.sample.rps.data.Game;
import arke.sample.rps.data.Player;
import arke.sample.rps.data.ormlite.ORMLiteActionDAO;
import arke.sample.rps.data.ormlite.ORMLiteGameDAO;
import arke.sample.rps.data.ormlite.ORMLitePlayerDAO;
import arke.velocity.android.AndroidVelocityUtils;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;

public class RockPaperScissorsService extends Service {

    //public static final String DATABASE_NAME = "/sdcard/rps";
    public static final int DATABASE_VERSION = 6;
    public static final String ENCODING = "utf-8";
    public static final String MIME_TYPE = ContentTypeUtils.MIME_TYPE_TEXT_PLAIN;


    public static final Class<?>[] CLASSES = {
            Action.class,
            Player.class,
            Game.class,

            SentMessage.class,
            SentMessagePart.class,

            PersistentUser.class,
            PersistentDevice.class,
            PersistentInboundMessage.class,
            PersistentOutboundMessage.class,
            PersistentScheduledMessage.class,
            PersistentMessagePart.class,
            PersistentUserProperty.class,
            PersistentDeviceProperty.class
    };

    private JPAContainer container;
    private AndroidSMSDeliveryHandler smsDeliveryHandler;
    private VelocityEngine velocityEngine;
    private VelocityContext velocityContext;
    private AndroidIntentDeliveryHandler intentDeliveryHandler;


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getName(), "binding "+getClass().getName());
        return new Binder() {
            // do nothing, nothing actually binds to this

        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int result = super.onStartCommand(intent, flags, startId);
        try {
            this.intentDeliveryHandler.deliverIntent(intent);
        } catch( Exception ex ) {
            Log.e(getClass().getName(), "unable to handle intent "+intent, ex);
        }

        return result;
    }

    @Override
    public void onCreate() {
        Log.e(getClass().getName(), "creating "+getClass().getName());
        super.onCreate();

        this.velocityEngine = new VelocityEngine();
        AndroidVelocityUtils.setupEngine(this, this.velocityEngine, true);
        this.velocityContext = new VelocityContext();
        this.velocityContext.put("math", new MathTool());
        this.velocityContext.put("date", new DateTool());

        Properties properties = new Properties();
        String databaseName;
        try {
            properties.load(getResources().openRawResource(R.raw.client));
            databaseName = properties.getProperty("databasename");
            boolean external = "true".equalsIgnoreCase(properties.getProperty("external"));
            if( external && databaseName != null ) {
                File externalFilesDir = getExternalFilesDir(null);
                if( externalFilesDir == null ) {
                    externalFilesDir = Environment.getExternalStorageDirectory();
                    if( externalFilesDir != null ) {
                        externalFilesDir = new File(externalFilesDir, getPackageName());
                    }
                }
                if( externalFilesDir != null ) {
                    externalFilesDir.mkdirs();
                    String path = externalFilesDir.getAbsolutePath();
                    if( !path.endsWith("/") ) {
                        path += "/";
                    }
                    databaseName = path + databaseName;
                } else {
                    Log.w(getClass().getName(), "unable to find external storage");
                }
            }
        } catch( Exception ex ) {
            Log.e(getClass().getName(), "unable to load properties, using defaults", ex);
            databaseName = null;
        }
        if( databaseName == null ) {
            databaseName = "rps";
        }
        Log.d(getClass().getName(), "writing database to "+databaseName);

        TimeZone timeZone = TimeZone.getDefault();

        OrmLiteSqliteOpenHelper helper = new OrmLiteSqliteOpenHelper(
                this,
                databaseName,
                null,
                DATABASE_VERSION
        ) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
                for( int i=0; i<CLASSES.length; i++ )
                {
                    Class<?> dataClass = CLASSES[i];
                    try
                    {
                        TableUtils.createTableIfNotExists(connectionSource, dataClass);
                    }
                    catch( Exception ex )
                    {
                        throw new RuntimeException("unable to create table for "+dataClass, ex);
                          // ignore any errors, we actually anticipate errors merging into the existing database
                          //Log.w(getClass().getSimpleName(), "trouble creating table for "+dataClass.getSimpleName()+" ignoring", ex);
                    }
                }

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int fromVersion, int toVersion) {
                for( int i=0; i<CLASSES.length; i++ )
                {
                    Class<?> dataClass = CLASSES[i];
                    try
                    {
                        TableUtils.dropTable(connectionSource, dataClass, true);
                    }
                    catch( Exception ex )
                    {
                          // ignore any errors, we actually anticipate errors merging into the existing database
                          Log.w(getClass().getSimpleName(), "trouble deleting table for "+dataClass.getSimpleName()+" ignoring", ex);
                    }
                }


                // TODO probably not the most elegant way of doing things
                onCreate(sqLiteDatabase, connectionSource);
            }
        };
        try {
            ORMLiteActionDAO actionDao = new ORMLiteActionDAO((Dao<Action, Integer>)helper.getDao(Action.class));
            ORMLitePlayerDAO playerDao = new ORMLitePlayerDAO((Dao<Player, Integer>)helper.getDao(Player.class));
            ORMLiteGameDAO gameDao = new ORMLiteGameDAO((Dao<Game, Integer>)helper.getDao(Game.class));

            ORMLiteSentMessageDAO sentMessageDao = new ORMLiteSentMessageDAO((Dao<SentMessage, Integer>)helper.getDao(SentMessage.class));
            ORMLiteSentMessagePartDAO sentMessagePartDao = new ORMLiteSentMessagePartDAO((Dao<SentMessagePart, Integer>)helper.getDao(SentMessagePart.class));

            ORMLitePersistentUserDAO userDao = new ORMLitePersistentUserDAO((Dao<PersistentUser, Integer>)helper.getDao(PersistentUser.class));
            ORMLitePersistentDeviceDAO deviceDao = new ORMLitePersistentDeviceDAO((Dao<PersistentDevice, Integer>)helper.getDao(PersistentDevice.class));
            ORMLitePersistentInboundMessageDAO inboundMessageDao = new ORMLitePersistentInboundMessageDAO((Dao<PersistentInboundMessage, Integer>)helper.getDao(PersistentInboundMessage.class));
            ORMLitePersistentOutboundMessageDAO outboundMessageDao = new ORMLitePersistentOutboundMessageDAO((Dao<PersistentOutboundMessage, Integer>)helper.getDao(PersistentOutboundMessage.class));
            ORMLitePersistentScheduledMessageDAO scheduledMessageDao = new ORMLitePersistentScheduledMessageDAO((Dao<PersistentScheduledMessage, Integer>)helper.getDao(PersistentScheduledMessage.class));
            ORMLitePersistentMessagePartDAO messagePartDao = new ORMLitePersistentMessagePartDAO((Dao<PersistentMessagePart, Integer>)helper.getDao(PersistentMessagePart.class), outboundMessageDao.getDao());
            ORMLitePersistentUserPropertyDAO userPropertyDao = new ORMLitePersistentUserPropertyDAO((Dao<PersistentUserProperty, Integer>)helper.getDao(PersistentUserProperty.class));
            ORMLitePersistentDevicePropertyDAO devicePropertyDao = new ORMLitePersistentDevicePropertyDAO((Dao<PersistentDeviceProperty, Integer>)helper.getDao(PersistentDeviceProperty.class));

            DeviceBasedTimeZoneGuesser timeZoneGuesser = new MobileNumberBasedTimeZoneGuesser(
                    PhoneNumberOfflineGeocoder.getInstance(),
                    PhoneNumberUtil.getInstance(),
                    Locale.getDefault().getCountry()
            );

            RockPaperScissorsUniverse universe = new RockPaperScissorsUniverse(
                    playerDao,
                    gameDao,
                    actionDao,
                    // 5 minutes
                    1000 * 60 * 5,
                    timeZone
            );

            // set up all the transformers
            HashMap<String, MessagePartTransformer> transformers = new HashMap<String, MessagePartTransformer>();
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_ALREADY_EXISTS);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_LONG);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_SHORT);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_CREATE_PLAYER_SUCCESS);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_LOBBY_ENTERED);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_NO_SUCH_PLAYER);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAY_PLAYER_MOVED);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAY_PLAYER_REMOVED);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAY_TURN_START);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAY_WIN);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_GAME_STARTED);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_TIMEZONE_SET);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_LOBBY_UNRECOGNISED_COMMAND);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAY_UNRECOGNISED_COMMAND);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_LEFT_GAME);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAYER_BUSY);
            addSMSTransformer(transformers, RockPaperScissorsUniverse.TEMPLATE_ID_PLAYER_INACTIVE);

            MessagePartTransformerUniverseAdapter templateUniverseAdapter = new MessagePartTransformerUniverseAdapter(
                    universe,
                    transformers
            );

            TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            String sourceAddress = tMgr.getLine1Number();

            SmsManager smsManager = SmsManager.getDefault();


            final Messenger smsMessenger = new AndroidSMSMessenger(
                    this,
                    sourceAddress,
                    smsManager,
                    sentMessageDao,
                    sentMessagePartDao
            );
            Messenger intentMessenger = new AndroidIntentMessenger(this);
            Messenger messenger = new DelegatingMessenger(
                    new HashMap<String, Messenger>(1){{
                        put(AndroidSMSConstants.DEVICE_TYPE_MOBILE, smsMessenger);
                    }},
                    intentMessenger
            );

            this.container = new JPAContainer(
                    Executors.newSingleThreadExecutor(),
                    templateUniverseAdapter,
                    messenger,
                    timeZone,
                    userDao,
                    deviceDao,
                    inboundMessageDao,
                    outboundMessageDao,
                    scheduledMessageDao,
                    messagePartDao,
                    userPropertyDao,
                    devicePropertyDao,
                    timeZoneGuesser
            );

            this.smsDeliveryHandler = new AndroidSMSDeliveryHandler(
                    this,
                    this.container,
                    sentMessagePartDao,
                    deviceDao
            );
            this.intentDeliveryHandler = new AndroidIntentDeliveryHandler(
                    container,
                    deviceDao
            );

            Log.e(getClass().getName(), "created "+getClass().getName());

        } catch (SQLException ex) {
            // what are you going to do?!
            Log.e(getClass().getSimpleName(), "unable to create", ex);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(getClass().getName(), "starting "+getClass().getName());

        if( this.container != null && this.smsDeliveryHandler != null ) {
            this.container.start();
            this.smsDeliveryHandler.start();
        }
        super.onStart(intent, startId);
        Log.e(getClass().getName(), "started " + getClass().getName());

    }

    @Override
    public void onDestroy() {
        Log.e(getClass().getName(), "destroying "+getClass().getName());
        if( this.container != null && this.smsDeliveryHandler != null ) {
            this.smsDeliveryHandler.stop();
            this.container.stop();
        }
        super.onDestroy();
        Log.e(getClass().getName(), "destroyed " + getClass().getName());

    }

    private void addSMSTransformer(Map<String, MessagePartTransformer> transformers, String key) {
        Template template = this.velocityEngine.getTemplate("sms_"+key);
        transformers.put(
                key,
                new VelocityMessagePartTransformer(
                        template,
                        MIME_TYPE,
                        ENCODING,
                        this.velocityContext
                )
        );
    }


}
