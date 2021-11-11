package Base;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Bot extends ListenerAdapter {

    public static JDA jda = null;
    public static SlashCommandManager slashCommandManager;
    public static String TOKEN;
    public static String CC_EMAIL;
    public static String CC_PW;

    public static void main(String[] args) throws LoginException {
        registerSecrets();
        jda = JDABuilder.create(Bot.TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(new Bot())
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        slashCommandManager = new SlashCommandManager(jda,null);
        BotUtils.switchPresence(jda);

        //remove cmds
        //jda.updateCommands().queue();
        //jda.getGuildById(684446613028077639L).updateCommands().queue();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        new Thread(() -> {
            slashCommandManager.runCommand(event);
            System.gc();
        }).start();
    }

    private static void registerSecrets(){
        File secrets_file = new File(System.getProperty("user.dir")+"/secrets.xml");
        TOKEN = System.getenv("TOKEN");
        CC_EMAIL = System.getenv("CC_EMAIL");
        CC_PW = System.getenv("CC_PW");
        if(TOKEN == null){
            if(secrets_file.exists()){

                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(secrets_file);
                    doc.getDocumentElement().normalize();
                    TOKEN = doc.getElementsByTagName("TOKEN").item(0).getTextContent();
                    CC_EMAIL = doc.getElementsByTagName("CC_EMAIL").item(0).getTextContent();
                    CC_PW = doc.getElementsByTagName("CC_PW").item(0).getTextContent();

                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.gc();
    }
}