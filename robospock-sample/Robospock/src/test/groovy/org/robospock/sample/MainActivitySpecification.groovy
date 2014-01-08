package org.robospock.sample

import android.widget.TextView
import org.jakubczyk.robotest.R
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification;

@Config(manifest = "./robospock-sample/AndroidSampleProject/src/main/AndroidManifest.xml")
class MainActivitySpecification extends RoboSpecification {

    def "Should say hello world"() {
        setup:
        def mainActivity = Robolectric.buildActivity(MainActivity.class).create().get()

        when:
        def tv = (TextView) mainActivity.findViewById(R.id.tv)

        then:
        "Hello world!" == tv.getText()
    }

}