package ca.tweetzy.skulls.menu.input;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.settings.Translations;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * Paper dialog-based text input.
 *
 * <p>Uses the native dialog API instead of intercepting chat messages, so it works
 * on Canvas/Folia and does not conflict with other chat plugins.</p>
 */
public final class InputDialogs {

	private InputDialogs() {
	}

	public static void text(Player player, String title, String label, String initial, int maxLength, Consumer<String> onSubmit) {
		if (player == null) return;

		final Component titleComponent = component(title);
		final Component labelComponent = component(label);
		final Component confirmComponent = component(TranslationManager.string(player, Translations.DIALOG_CONFIRM));

		final Dialog dialog = Dialog.create(factory -> {
			final TextDialogInput input = DialogInput.text("value", labelComponent)
					.width(300)
					.maxLength(Math.max(1, Math.min(256, maxLength)))
					.initial(initial == null ? "" : initial)
					.build();

			final DialogBase base = DialogBase.builder(titleComponent)
					.inputs(List.of(input))
					.canCloseWithEscape(true)
					.pause(false)
					.afterAction(DialogBase.DialogAfterAction.CLOSE)
					.build();

			final ActionButton submit = ActionButton.builder(confirmComponent)
					.width(150)
					.action(DialogAction.customClick((response, audience) -> {
						final String value = response.getText("value");
						if (!(audience instanceof Player target)) return;

						Skulls.getTaskScheduler().runTask(target, () -> {
							if (!target.isOnline()) return;
							onSubmit.accept(value == null ? "" : value);
						});
					}, ClickCallback.Options.builder().uses(1).build()))
					.build();

			final MultiActionType type = DialogType.multiAction(List.of(submit)).build();
			factory.empty().base(base).type(type);
		});

		player.showDialog(dialog);
	}

	private static Component component(String text) {
		return Menu.LEGACY.deserialize(Common.colorize(text));
	}
}
