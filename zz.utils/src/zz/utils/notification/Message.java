/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 18, 2002
 * Time: 2:17:26 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Encapsulates messages transmitted to the notification center.
 * A message has a list of sources, which define the objects that have
 * been affected by the message.
 */
public class Message
{
	private List itsSources = new ArrayList ();

	public Message (Object aSource)
	{
		itsSources.add (aSource);
	}

	public Message (Collection aSources)
	{
		itsSources.addAll(aSources);
	}

	public List getSources ()
	{
		return itsSources;
	}

	public void addSource (Object aSource)
	{
		itsSources.add (aSource);
	}

	public void addSources (Collection aSources)
	{
		itsSources.addAll(aSources);
	}
}
