/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 18, 2002
 * Time: 2:27:52 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.notification;


/**
 * Classes interested in recieving {@link Message messages} should implement
 * this interface.
 * They must also register with a {@link NotificationCenter} or {@link NotificationManager}
 */
public interface Notifiable
{
	public void processMessage (Message aMessage);
}
