/*
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 25 sept. 2002
 * Time: 13:33:20
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.notification;

/**
 * This interface extends the notification mechanism by providing a priority to the notifiables.
 * Notifiables with higher priority will be notified first.
 * The default priority (ie, for {@link Notifiable}) is 0.
 */
public interface PrioritizedNotifiable extends Notifiable
{
	public int getPriority ();
}
