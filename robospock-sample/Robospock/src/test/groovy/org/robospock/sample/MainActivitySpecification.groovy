package org.robospock.sample


import android.widget.TextView

import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification;

import org.robospock.sample.MainActivity
import org.robospock.sample.R


@Config(manifest = "./robospock-sample/AndroidSampleProject/src/main/AndroidManifest.xml")
class MainActivitySpecification extends RoboSpecification {

    def "Should say hello world"() {
        setup:
        def mainActivity = Robolectric.buildActivity(MainActivity.class).create().get()

        when:
        def tv = (TextView) mainActivity.findViewById(R.id.tv)

        then:
        "Account{name='JohnWhite', password='24a28dbe'}" == tv.getText()
    }

}