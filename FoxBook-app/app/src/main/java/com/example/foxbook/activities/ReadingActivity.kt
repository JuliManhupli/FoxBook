package com.example.foxbook.activities

import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foxbook.R
import java.security.AccessController.getContext


class ReadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        val bookText: TextView = findViewById(R.id.txtBookText)

        val txt = "Ворожий обстріл Києва: ДСНС та поліція працюють на місцях подій \n" +
                "\n" +
                "\uD83D\uDD34 У Дніпровському районі внаслідок ворожого обстрілу пошкоджено фасад багатоповерхівки, сталося загоряння балкона на 6 поверсі. На момент прибуття пожежних підрозділів у дворі будинку палало 8 автівок. \n" +
                "\n" +
                "Загалом було врятовано 17 осіб, з яких 5 людей, які не могли самостійно пересуватися, та 7 дітей. На безпечну відстань евакуйовано 25 осіб. \n" +
                "\n" +
                "Також пошкоджень зазнала будівля садочку. Понівечено фасад будівлі, вибиті вікна. Всередині перебував охоронець. Наразі його життю нічого не загрожує. \n" +
                "\n" +
                "За інформацією поліції, постраждало 34 особи. 15 - госпіталізовано. Попередні діагнози: колото-різані рани та гостра реакція на стрес.\n" +
                "\n" +
                "На місці події працює психологічна служба ДСНС, також рятувальники розгорнули пересувні Пункти Незламності. \n" +
                "\n" +
                "Поліція взяла під охорону пошкоджену багатоповерхівку, зокрема для збереження майна евакуйованих громадян. \n" +
                "\n" +
                "За іншою адресою у цьому ж районі виникла пожежа у приватному житловому будинку, на площі 2 кв. м.\n" +
                "\n" +
                "\uD83D\uDD34 У Дарницькому районі внаслідок ворожого обстрілу сталося загоряння житлового будинку на площі 400 кв. м. Пожежу ліквідовано. \n" +
                "\n" +
                "\uD83D\uDD34 Ще за двома адресами в Деснянському районі було виявлено падіння уламків на відкритій території.\n" +
                "\n" +
                "Зафіксовано падіння уламків і в інших районах столиці. Підрозділи ДСНС не залучались, жертв та постраждалих немає."


        val measurePaint = Paint(bookText.getPaint())
        var pWidth = measurePaint.measureText(txt)
        val labelWidth: Int = bookText.getWidth()
        val maxLines: Int = bookText.getMaxLines()

        while (labelWidth > 0 && pWidth / maxLines > labelWidth - 20) {
            val textSize = measurePaint.textSize
            measurePaint.textSize = textSize - 1
            pWidth = measurePaint.measureText(txt)
            if (textSize < TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 7f,
                    resources.displayMetrics
                )
            ) break
        }

        bookText.setTextSize(TypedValue.COMPLEX_UNIT_PX, measurePaint.textSize)
    }
}